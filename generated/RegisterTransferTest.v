module RegisterTransferTest(
  input         clock,
  input         reset,
  input         io_tricleLeft,
  input  [1:0]  io_address,
  output [31:0] io_readData1,
  output [31:0] io_readData2,
  output [31:0] io_readData3
);
`ifdef RANDOMIZE_REG_INIT
  reg [31:0] _RAND_0;
  reg [31:0] _RAND_1;
  reg [31:0] _RAND_2;
  reg [31:0] _RAND_3;
  reg [31:0] _RAND_4;
  reg [31:0] _RAND_5;
`endif // RANDOMIZE_REG_INIT
  reg [31:0] R1_0; // @[RegisterTransferTest.scala 14:15]
  reg [31:0] R1_1; // @[RegisterTransferTest.scala 14:15]
  reg [31:0] R2_0; // @[RegisterTransferTest.scala 15:15]
  reg [31:0] R2_1; // @[RegisterTransferTest.scala 15:15]
  reg [31:0] R3_0; // @[RegisterTransferTest.scala 16:15]
  reg [31:0] R3_1; // @[RegisterTransferTest.scala 16:15]
  assign io_readData1 = io_address[0] ? R1_1 : R1_0; // @[RegisterTransferTest.scala 36:16]
  assign io_readData2 = io_address[0] ? R2_1 : R2_0; // @[RegisterTransferTest.scala 37:16]
  assign io_readData3 = io_address[0] ? R3_1 : R3_0; // @[RegisterTransferTest.scala 38:16]
`ifdef RANDOMIZE_GARBAGE_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_INVALID_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_REG_INIT
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_MEM_INIT
`define RANDOMIZE
`endif
`ifndef RANDOM
`define RANDOM $random
`endif
`ifdef RANDOMIZE_MEM_INIT
  integer initvar;
`endif
`ifndef SYNTHESIS
`ifdef FIRRTL_BEFORE_INITIAL
`FIRRTL_BEFORE_INITIAL
`endif
initial begin
  `ifdef RANDOMIZE
    `ifdef INIT_RANDOM
      `INIT_RANDOM
    `endif
    `ifndef VERILATOR
      `ifdef RANDOMIZE_DELAY
        #`RANDOMIZE_DELAY begin end
      `else
        #0.002 begin end
      `endif
    `endif
`ifdef RANDOMIZE_REG_INIT
  _RAND_0 = {1{`RANDOM}};
  R1_0 = _RAND_0[31:0];
  _RAND_1 = {1{`RANDOM}};
  R1_1 = _RAND_1[31:0];
  _RAND_2 = {1{`RANDOM}};
  R2_0 = _RAND_2[31:0];
  _RAND_3 = {1{`RANDOM}};
  R2_1 = _RAND_3[31:0];
  _RAND_4 = {1{`RANDOM}};
  R3_0 = _RAND_4[31:0];
  _RAND_5 = {1{`RANDOM}};
  R3_1 = _RAND_5[31:0];
`endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`ifdef FIRRTL_AFTER_INITIAL
`FIRRTL_AFTER_INITIAL
`endif
`endif // SYNTHESIS
  always @(posedge clock) begin
    if (io_tricleLeft) begin
      R1_0 <= R2_0;
    end
    if (io_tricleLeft) begin
      R1_1 <= R2_1;
    end
    if (io_tricleLeft) begin
      R2_0 <= R3_0;
    end
    if (io_tricleLeft) begin
      R2_1 <= R3_1;
    end
    if (io_tricleLeft) begin
      R3_0 <= 32'h0;
    end else begin
      R3_0 <= 32'h14;
    end
    if (io_tricleLeft) begin
      R3_1 <= 32'h0;
    end else begin
      R3_1 <= 32'h14;
    end
  end
endmodule
