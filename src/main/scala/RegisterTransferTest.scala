import chisel3._

class RegisterTransferTest extends Module {
  val io = IO(new Bundle {
    //The following signals are used by the tester to load and dump the memory contents. Do not touch.
    val tricleLeft = Input(Bool ())
    val address = Input(UInt(2.W))
    val readData1 = Output(UInt(32.W))
    val readData2 = Output(UInt(32.W))
    val readData3 = Output(UInt(32.W))

  })

  val R1 = Reg(Vec(2, UInt(32.W)))
  val R2 = Reg(Vec(2, UInt(32.W)))
  val R3 = Reg(Vec(2, UInt(32.W)))
  R3(0.U(32.W)) := 20.U(32.W)
  R3(1.U(32.W)) := 20.U(32.W)
  val RegisterEmpty = Reg(Vec (2, UInt(32.W)))

  when(io.tricleLeft === true.B){
    R1 := R2
    R2 := R3
    R3 := RegisterEmpty

  }

  io.readData1 := R1(io.address)
  io.readData2 := R2(io.address)
  io.readData3 := R3(io.address)
}


