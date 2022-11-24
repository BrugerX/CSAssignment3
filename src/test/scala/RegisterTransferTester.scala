import chisel3.iotesters
import chisel3.iotesters.PeekPokeTester
import chisel3._


class RegisterTransferTester(dut: RegisterTransferTest) extends PeekPokeTester(dut) {
  poke(dut.io.tricleLeft, false.B)
  poke(dut.io.address,0)
  peek(dut.io.readData1)
  peek(dut.io.readData2)
  peek(dut.io.readData3)
  step(1)

  poke(dut.io.tricleLeft, true.B)
  poke(dut.io.address, 0)
  peek(dut.io.readData1)
  peek(dut.io.readData2)
  peek(dut.io.readData3)
  step(2)






}

object RegisterTransferTester {
  def main(args: Array[String]): Unit = {
    println("Running the Hello tester")
    iotesters.Driver.execute(
      Array("--generate-vcd-output", "on",
        "--target-dir", "generated",
        "--top-name", "Hello"),
      () => new RegisterTransferTest()) {
      c => new RegisterTransferTester(c)
    }
  }
}
