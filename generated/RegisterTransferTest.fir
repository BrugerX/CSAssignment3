;buildInfoPackage: chisel3, version: 3.3.2, scalaVersion: 2.12.11, sbtVersion: 1.3.10
circuit RegisterTransferTest : 
  module RegisterTransferTest : 
    input clock : Clock
    input reset : UInt<1>
    output io : {flip tricleLeft : UInt<1>, flip address : UInt<2>, readData1 : UInt<32>, readData2 : UInt<32>, readData3 : UInt<32>}
    
    reg R1 : UInt<32>[2], clock @[RegisterTransferTest.scala 14:15]
    reg R2 : UInt<32>[2], clock @[RegisterTransferTest.scala 15:15]
    reg R3 : UInt<32>[2], clock @[RegisterTransferTest.scala 16:15]
    R3[UInt<1>("h00")] <= UInt<32>("h014") @[RegisterTransferTest.scala 17:17]
    R3[UInt<1>("h01")] <= UInt<32>("h014") @[RegisterTransferTest.scala 18:17]
    reg RegisterEmpty : UInt<32>[2], clock @[RegisterTransferTest.scala 19:26]
    node _T = eq(io.tricleLeft, UInt<1>("h01")) @[RegisterTransferTest.scala 21:22]
    when _T : @[RegisterTransferTest.scala 21:33]
      R1[0] <= R2[0] @[RegisterTransferTest.scala 30:8]
      R1[1] <= R2[1] @[RegisterTransferTest.scala 30:8]
      R2[0] <= R3[0] @[RegisterTransferTest.scala 31:8]
      R2[1] <= R3[1] @[RegisterTransferTest.scala 31:8]
      R3[0] <= RegisterEmpty[0] @[RegisterTransferTest.scala 32:8]
      R3[1] <= RegisterEmpty[1] @[RegisterTransferTest.scala 32:8]
      skip @[RegisterTransferTest.scala 21:33]
    node _T_1 = bits(io.address, 0, 0)
    io.readData1 <= R1[_T_1] @[RegisterTransferTest.scala 36:16]
    node _T_2 = bits(io.address, 0, 0)
    io.readData2 <= R2[_T_2] @[RegisterTransferTest.scala 37:16]
    node _T_3 = bits(io.address, 0, 0)
    io.readData3 <= R3[_T_3] @[RegisterTransferTest.scala 38:16]
    
