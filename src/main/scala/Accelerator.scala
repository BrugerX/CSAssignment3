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
  val y_0 = RegInit(0.U(32.W))
  val x_0 = RegInit(1.U(32.W))
  val x_1 = RegInit(1.U(32.W))
  val y_1 = RegInit(1.U(32.W))
  val whichRs = RegInit(0.U(3.W)) //Used to save to the correct star Register
  val Rs =  RegInit(VecInit(Seq.fill(5)(0.U(2.W)))) //The star register





  //c) truth_value
  val c = RegInit(true.B)
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

  //Default values
  io.dataWrite := 0.U
  io.address := x_0 + y_0*20.U
  io.writeEnable := false.B
  io.done := false.B



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
        io.address := (x_0) + (y_0+1.U) * 20.U  //We load the bit to the right of this

        //Start(x_0,y_0)

        //a) Operation - y_0 = +1
        Rs(0.U(2.W)) := Rc(y_0 + 1.U)
        Rs(1.U) := Rc(y_0)
        Rs(2.U) := Rc(y_0 + 2.U(2.W))
        Rs(3.U) := Rl(y_0+1.U)
        //Rs(4.U) := Rr(y_0+1.U) This will overwrite the selected value


        //Load and set Rr
        io.writeEnable := false.B
        when(io.dataRead === 255.U) {
          Rs(4.U) := 2.U(2.W)
          Rr(y_0+1.U) := 1.U(2.W)
        }.elsewhen(io.dataRead === 0.U) {
          Rs(4.U) := 1.U(2.W)
          Rr(y_0+1.U) := 2.U(2.W)
        }

      } .elsewhen((y_0 === n-2.U)&(x_0 =/= n-2.U)){ //Hvis vi er i bunden men ikke hjørnet
        //b) operation - trickle left
        Rl := Rc
        Rc := Rr
        Rr := Zero_Vector

        //We start at the top again and move right
        y_0 := 1.U(32.W)
        x_0 := x_0 + 1.U(32.W)
        io.address := (x_0 + 1.U) + (1.U) * 20.U  //We load the bit to the right of this

        //Start(x_0,y_0)

        //a) Operation - y_0 = 1
        Rs(0.U(2.W)) := Rc(1.U) //We have confirmed, that we can trickle left and read in the same cycle
        Rs(1.U) := Rc(0.U) //up
        Rs(2.U) := Rc(2.U) //down
        Rs(3.U) := Rl(1.U)  //left
        //Rs(4.U) := Rr(1.U) This will overwrite the selected value


        //Load and set Rr
        io.writeEnable := false.B
        when(io.dataRead === 255.U) {
          Rs(4.U) := 2.U(2.W)
          Rr(1.U) := 1.U(2.W)
        }.elsewhen(io.dataRead === 0.U) {
          Rs(4.U) := 1.U(2.W)
          Rr(1.U) := 2.U(2.W)
        }

      }.elsewhen((y_0 === n-2.U)&(x_0 === n-2.U)){ //Hvis vi er i hjørnet
        io.done := true.B
        stateReg := done
      }

      c := cMux(0)||cMux(1)||cMux(2)||cMux(3)||cMux(4)
      d := dMux(0)||dMux(1)||dMux(2)||dMux(3)||dMux(4)

      stateReg := checking
    }

    is(checking) {



      when(cMux(0)||cMux(1)||cMux(2)||cMux(3)||cMux(4)){ //c)
        //Save
        io.address := x_0 + y_0 * n + 400.U
        io.writeEnable := true.B
        io.dataWrite := 0.U(32.W)
        stateReg := move
      } .elsewhen(dMux(0)||dMux(1)||dMux(2)||dMux(3)||dMux(4)){ //d)
        when(dMux(0.U)){ //We don't know about center
          io.address := x_0 + y_0 * n
          when(io.dataRead === 0.U(32.W)){
            Rs(0.U) := 1.U(32.W)
            Rc(y_0) := 1.U(32.W)
          }.elsewhen(io.dataRead =/= 0.U(32.W)){
            Rs(0.U) := 2.U(32.W)
            Rc(y_0) := 2.U(32.W)
          }
        } .elsewhen(!dMux(0.U) & dMux(1.U)) { //We know about center, not above
          io.address := x_0 + (y_0-1.U) * n
          when(io.dataRead === 0.U(32.W)) {
            Rs(1.U) := 1.U(32.W)
            Rc(y_0-1.U) := 1.U(32.W)
          }.elsewhen(io.dataRead =/= 0.U(32.W)) {
            Rs(1.U) := 2.U(32.W)
            Rc(y_0-1.U) := 2.U(32.W)
          }
        } .elsewhen(!dMux(0.U) & !dMux(1.U) & dMux(2.U)){ //We don't know about below
          io.address := x_0 + (y_0 + 1.U) * n
          when(io.dataRead === 0.U(32.W)) {
            Rs(2.U) := 1.U(32.W)
            Rc(y_0 + 1.U) := 1.U(32.W)
          }.elsewhen(io.dataRead =/= 0.U(32.W)) {
            Rs(2.U) := 2.U(32.W)
            Rc(y_0+ 1.U) := 2.U(32.W)
          }
        } .elsewhen(!dMux(0.U) & !dMux(1.U) & !dMux(2.U)){ //We don't know about left
          io.address := (x_0-1.U) + y_0 * n
          when(io.dataRead === 0.U(32.W)) {
            Rs(3.U) := 1.U(32.W)
            Rl(y_0) := 1.U(32.W)
          }.elsewhen(io.dataRead =/= 0.U(32.W)) {
            Rs(3.U) := 2.U(32.W)
            Rl(y_0) := 2.U(32.W)
          }
        }
        io.writeEnable := false.B
        stateReg := checking


      } .elsewhen(!(dMux(0)||dMux(1)||dMux(2)||dMux(3)||dMux(4)) && !(cMux(0)||cMux(1)||cMux(2)||cMux(3)||cMux(4))){ //Neg(d) AND neg(c)
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
