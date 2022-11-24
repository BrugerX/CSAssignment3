import chisel3._
import chisel3.util._

class Accelerator extends Module {
  val io = IO(new Bundle {
    val start = Input(Bool())
    val done = Output(Bool())

    val address = Output(UInt (16.W))
    val dataRead = Input(UInt (32.W))
    val writeEnable = Output(Bool ())
    val dataWrite = Output(UInt (32.W))

  })

  //Initial
  val idle :: move :: checking :: done :: Nil = Enum (4)
  val stateReg = RegInit(idle)

  //Registers
  val n = 20.U(32.W)
  val Rl = RegInit(VecInit(Seq.fill(20)(0.U(2.W))))
  val Rc = RegInit(VecInit(Seq.fill(20)(0.U(2.W))))
  val Rr = RegInit(VecInit(Seq.fill(20)(0.U(2.W))))
  val Zero_Vector = RegInit(VecInit(Seq.fill(20)(0.U(2.W))))

  //Coordinates
  val current_read = RegInit(0.U(2.W))
  val y_0 = RegInit(1.U(32.W))
  val x_0 = RegInit(1.U(32.W))
  val x_1 = RegInit(1.U(32.W))
  val y_1 = RegInit(1.U(32.W))
  val whichRs = RegInit(0.U(3.W)) //Used to save to the correct star Register
  val Rs =  RegInit(VecInit(Seq.fill(5)(0.U(2.W)))) //The star register





  //c) truth_value
  val c = RegInit(false.B)
  //c) circuit
  val cMux = Wire(Vec(5, Bool()))
  cMux(0) := Rs(0) === 1.U(2.W)
  cMux(1) := Rs(1) === 1.U(2.W)
  cMux(2) := Rs(2) === 1.U(2.W)
  cMux(3) := Rs(3) === 1.U(2.W)
  cMux(4) := Rs(4) === 1.U(2.W)

  //d) truth value
  val d = RegInit(false.B)
  //d) circuit
  val dMux = Wire(Vec(5, Bool()))
  dMux(0) := Rs(0) === 0.U(2.W)
  dMux(1) := Rs(1) === 0.U(2.W)
  dMux(2) := Rs(2) === 0.U(2.W)
  dMux(3) := Rs(3) === 0.U(2.W)
  dMux(4) := Rs(4) === 0.U(2.W)



  //FSMD switch
  switch(stateReg) {
    is(idle) {
      when(io.start) {
        stateReg := move
      }
    }

    is(move) {
      io.writeEnable := false.B

      when((y_0 =/= n-2.U)){ //Hvis vi ikke er i bunden eller hjørnet
        y_0 :=  y_0 + 1.U
      } .elsewhen((y_0 === n-2.U)&(x_0 =/= n-2.U)){ //Hvis vi er i bunden men ikke hjørnet
        //b) operation - trickle left
        Rl := Rc
        Rc := Rr
        Rr := Zero_Vector

        //We start at the top again and move right
        y_0 := 1.U(32.W)
        x_0 := x_0 + 1.U(32.W)
      }.elsewhen((y_0 === n-2.U)&(x_0 === n-2.U)){ //Hvis vi er i hjørnet
        io.done := true.B
        stateReg := done
      }

      //Start(x_0,y_0)

      //a) Operation
      Rs(0.U(2.W)) := Rc(y_0)
      Rs(1.U) := Rc(y_0 - 1.U(2.W))
      Rs(2.U) := Rc(y_0 + 1.U(2.W))
      Rs(3.U) := Rl(y_0)
      Rs(4.U) := Rr(y_0)

      //Load
      io.address := x_0 + y_0 * n
      io.writeEnable := false.B
      when(io.dataRead === 255.U(32.W)) {
        current_read := 2.U(2.W)
      }.elsewhen(io.dataRead === 0.U(32.W)) {
        current_read := 1.U(2.W)
      }

      //Set right register
      Rs(4.U) := current_read
      Rr(y_0) := Rs(4.U)

      stateReg := checking
    }

    is(checking) {

      c := cMux(0)|cMux(1)|cMux(2)|cMux(3)|cMux(4)
      d := dMux(0)|dMux(1)|dMux(2)|dMux(3)|dMux(4)

      when(c){
        //Save
        io.address := x_0 + y_0 * n + 400.U
        io.writeEnable := true.B
        io.dataWrite := 0.U(32.W)
        stateReg := move
      } .elsewhen(d){
        when(dMux(0.U)){ //We don't know about center
          x_1 := x_0
          y_1 := y_0
          whichRs := 0.U
        } .elsewhen(!dMux(0.U) & dMux(1.U)) { //We know about center, not above
          x_1 := x_0
          y_1 := y_0 - 1.U
          whichRs := 1.U
        } .elsewhen(!dMux(0.U) & !dMux(1.U) & dMux(2.U)){
          x_1 := x_0
          y_1 := y_0 + 1.U
          whichRs := 2.U
        } .elsewhen(!dMux(0.U) & !dMux(1.U) & !dMux(2.U)){
          x_1 := x_0 - 1.U
          y_1 := y_0
          whichRs := 3.U
        }

        io.address := x_1 + y_1 * n
        Rs(whichRs) := io.dataRead

        when(whichRs === 3.U){
          Rl(y_1) := Rs(whichRs)
        }.elsewhen(whichRs =/= 3.U){
          Rc(y_1) := Rs(whichRs)
        }

        stateReg := checking


      } .elsewhen(!d & !c){
        //Save
        io.address := x_0 + y_0 * n + 400.U
        io.writeEnable := true.B
        io.dataWrite := 255.U(32.W)
        stateReg := move
      }




    }

    is(done) {
      io.writeEnable := false.B
      io.done := true.B
      stateReg := done
    }
  }



}
