begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.examples.terasort
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|examples
operator|.
name|terasort
package|;
end_package

begin_comment
comment|/**  * This class implements a 128-bit linear congruential generator.  * Specifically, if X0 is the most recently issued 128-bit random  * number (or a seed of 0 if no random number has already been generated,  * the next number to be generated, X1, is equal to:  * X1 = (a * X0 + c) mod 2**128  * where a is 47026247687942121848144207491837523525  *            or 0x2360ed051fc65da44385df649fccf645  *   and c is 98910279301475397889117759788405497857  *            or 0x4a696d47726179524950202020202001  * The coefficient "a" is suggested by:  * Pierre L'Ecuyer, "Tables of linear congruential generators of different  * sizes and good lattice structure", Mathematics of Computation, 68  * pp. 249 - 260 (1999)  * http://www.ams.org/mcom/1999-68-225/S0025-5718-99-00996-5/S0025-5718-99-00996-5.pdf  * The constant "c" meets the simple suggestion by the same reference that  * it be odd.  *  * There is also a facility for quickly advancing the state of the  * generator by a fixed number of steps - this facilitates parallel  * generation.  *  * This is based on 1.0 of rand16.c from Chris Nyberg   *<chris.nyberg@ordinal.com>.  */
end_comment

begin_class
DECL|class|Random16
class|class
name|Random16
block|{
comment|/**     * The "Gen" array contain powers of 2 of the linear congruential generator.    * The index 0 struct contain the "a" coefficient and "c" constant for the    * generator.  That is, the generator is:    *    f(x) = (Gen[0].a * x + Gen[0].c) mod 2**128    *    * All structs after the first contain an "a" and "c" that    * comprise the square of the previous function.    *    * f**2(x) = (Gen[1].a * x + Gen[1].c) mod 2**128    * f**4(x) = (Gen[2].a * x + Gen[2].c) mod 2**128    * f**8(x) = (Gen[3].a * x + Gen[3].c) mod 2**128    * ...     */
DECL|class|RandomConstant
specifier|private
specifier|static
class|class
name|RandomConstant
block|{
DECL|field|a
specifier|final
name|Unsigned16
name|a
decl_stmt|;
DECL|field|c
specifier|final
name|Unsigned16
name|c
decl_stmt|;
DECL|method|RandomConstant (String left, String right)
specifier|public
name|RandomConstant
parameter_list|(
name|String
name|left
parameter_list|,
name|String
name|right
parameter_list|)
block|{
name|a
operator|=
operator|new
name|Unsigned16
argument_list|(
name|left
argument_list|)
expr_stmt|;
name|c
operator|=
operator|new
name|Unsigned16
argument_list|(
name|right
argument_list|)
expr_stmt|;
block|}
block|}
DECL|field|genArray
specifier|private
specifier|static
specifier|final
name|RandomConstant
index|[]
name|genArray
init|=
operator|new
name|RandomConstant
index|[]
block|{
comment|/* [  0] */
operator|new
name|RandomConstant
argument_list|(
literal|"2360ed051fc65da44385df649fccf645"
argument_list|,
literal|"4a696d47726179524950202020202001"
argument_list|)
block|,
comment|/* [  1] */
operator|new
name|RandomConstant
argument_list|(
literal|"17bce35bdf69743c529ed9eb20e0ae99"
argument_list|,
literal|"95e0e48262b3edfe04479485c755b646"
argument_list|)
block|,
comment|/* [  2] */
operator|new
name|RandomConstant
argument_list|(
literal|"f4dd417327db7a9bd194dfbe42d45771"
argument_list|,
literal|"882a02c315362b60765f100068b33a1c"
argument_list|)
block|,
comment|/* [  3] */
operator|new
name|RandomConstant
argument_list|(
literal|"6347af777a7898f6d1a2d6f33505ffe1"
argument_list|,
literal|"5efc4abfaca23e8ca8edb1f2dfbf6478"
argument_list|)
block|,
comment|/* [  4] */
operator|new
name|RandomConstant
argument_list|(
literal|"b6a4239f3b315f84f6ef6d3d288c03c1"
argument_list|,
literal|"f25bd15439d16af594c1b1bafa6239f0"
argument_list|)
block|,
comment|/* [  5] */
operator|new
name|RandomConstant
argument_list|(
literal|"2c82901ad1cb0cd182b631ba6b261781"
argument_list|,
literal|"89ca67c29c9397d59c612596145db7e0"
argument_list|)
block|,
comment|/* [  6] */
operator|new
name|RandomConstant
argument_list|(
literal|"dab03f988288676ee49e66c4d2746f01"
argument_list|,
literal|"8b6ae036713bd578a8093c8eae5c7fc0"
argument_list|)
block|,
comment|/* [  7] */
operator|new
name|RandomConstant
argument_list|(
literal|"602167331d86cf5684fe009a6d09de01"
argument_list|,
literal|"98a2542fd23d0dbdff3b886cdb1d3f80"
argument_list|)
block|,
comment|/* [  8] */
operator|new
name|RandomConstant
argument_list|(
literal|"61ecb5c24d95b058f04c80a23697bc01"
argument_list|,
literal|"954db923fdb7933e947cd1edcecb7f00"
argument_list|)
block|,
comment|/* [  9] */
operator|new
name|RandomConstant
argument_list|(
literal|"4a5c31e0654c28aa60474e83bf3f7801"
argument_list|,
literal|"00be4a36657c98cd204e8c8af7dafe00"
argument_list|)
block|,
comment|/* [ 10] */
operator|new
name|RandomConstant
argument_list|(
literal|"ae4f079d54fbece1478331d3c6bef001"
argument_list|,
literal|"991965329dccb28d581199ab18c5fc00"
argument_list|)
block|,
comment|/* [ 11] */
operator|new
name|RandomConstant
argument_list|(
literal|"101b8cb830c7cb927ff1ed50ae7de001"
argument_list|,
literal|"e1a8705b63ad5b8cd6c3d268d5cbf800"
argument_list|)
block|,
comment|/* [ 12] */
operator|new
name|RandomConstant
argument_list|(
literal|"f54a27fc056b00e7563f3505e0fbc001"
argument_list|,
literal|"2b657bbfd6ed9d632079e70c3c97f000"
argument_list|)
block|,
comment|/* [ 13] */
operator|new
name|RandomConstant
argument_list|(
literal|"df8a6fc1a833d201f98d719dd1f78001"
argument_list|,
literal|"59b60ee4c52fa49e9fe90682bd2fe000"
argument_list|)
block|,
comment|/* [ 14] */
operator|new
name|RandomConstant
argument_list|(
literal|"5480a5015f101a4ea7e3f183e3ef0001"
argument_list|,
literal|"cc099c88030679464fe86aae8a5fc000"
argument_list|)
block|,
comment|/* [ 15] */
operator|new
name|RandomConstant
argument_list|(
literal|"a498509e76e5d7925f539c28c7de0001"
argument_list|,
literal|"06b9abff9f9f33dd30362c0154bf8000"
argument_list|)
block|,
comment|/* [ 16] */
operator|new
name|RandomConstant
argument_list|(
literal|"0798a3d8b10dc72e60121cd58fbc0001"
argument_list|,
literal|"e296707121688d5a0260b293a97f0000"
argument_list|)
block|,
comment|/* [ 17] */
operator|new
name|RandomConstant
argument_list|(
literal|"1647d1e78ec02e665fafcbbb1f780001"
argument_list|,
literal|"189ffc4701ff23cb8f8acf6b52fe0000"
argument_list|)
block|,
comment|/* [ 18] */
operator|new
name|RandomConstant
argument_list|(
literal|"a7c982285e72bf8c0c8ddfb63ef00001"
argument_list|,
literal|"5141110ab208fb9d61fb47e6a5fc0000"
argument_list|)
block|,
comment|/* [ 19] */
operator|new
name|RandomConstant
argument_list|(
literal|"3eb78ee8fb8c56dbc5d4e06c7de00001"
argument_list|,
literal|"3c97caa62540f2948d8d340d4bf80000"
argument_list|)
block|,
comment|/* [ 20] */
operator|new
name|RandomConstant
argument_list|(
literal|"72d03b6f4681f2f9fe8e44d8fbc00001"
argument_list|,
literal|"1b25cb9cfe5a0c963174f91a97f00000"
argument_list|)
block|,
comment|/* [ 21] */
operator|new
name|RandomConstant
argument_list|(
literal|"ea85f81e4f502c9bc8ae99b1f7800001"
argument_list|,
literal|"0c644570b4a487103c5436352fe00000"
argument_list|)
block|,
comment|/* [ 22] */
operator|new
name|RandomConstant
argument_list|(
literal|"629c320db08b00c6bfa57363ef000001"
argument_list|,
literal|"3d0589c28869472bde517c6a5fc00000"
argument_list|)
block|,
comment|/* [ 23] */
operator|new
name|RandomConstant
argument_list|(
literal|"c5c4b9ce268d074a386be6c7de000001"
argument_list|,
literal|"bc95e5ab36477e65534738d4bf800000"
argument_list|)
block|,
comment|/* [ 24] */
operator|new
name|RandomConstant
argument_list|(
literal|"f30bbbbed1596187555bcd8fbc000001"
argument_list|,
literal|"ddb02ff72a031c01011f71a97f000000"
argument_list|)
block|,
comment|/* [ 25] */
operator|new
name|RandomConstant
argument_list|(
literal|"4a1000fb26c9eeda3cc79b1f78000001"
argument_list|,
literal|"2561426086d9acdb6c82e352fe000000"
argument_list|)
block|,
comment|/* [ 26] */
operator|new
name|RandomConstant
argument_list|(
literal|"89fb5307f6bf8ce2c1cf363ef0000001"
argument_list|,
literal|"64a788e3c118ed1c8215c6a5fc000000"
argument_list|)
block|,
comment|/* [ 27] */
operator|new
name|RandomConstant
argument_list|(
literal|"830b7b3358a5d67ea49e6c7de0000001"
argument_list|,
literal|"e65ea321908627cfa86b8d4bf8000000"
argument_list|)
block|,
comment|/* [ 28] */
operator|new
name|RandomConstant
argument_list|(
literal|"fd8a51da91a69fe1cd3cd8fbc0000001"
argument_list|,
literal|"53d27225604d85f9e1d71a97f0000000"
argument_list|)
block|,
comment|/* [ 29] */
operator|new
name|RandomConstant
argument_list|(
literal|"901a48b642b90b55aa79b1f780000001"
argument_list|,
literal|"ca5ec7a3ed1fe55e07ae352fe0000000"
argument_list|)
block|,
comment|/* [ 30] */
operator|new
name|RandomConstant
argument_list|(
literal|"118cdefdf32144f394f363ef00000001"
argument_list|,
literal|"4daebb2e085330651f5c6a5fc0000000"
argument_list|)
block|,
comment|/* [ 31] */
operator|new
name|RandomConstant
argument_list|(
literal|"0a88c0a91cff430829e6c7de00000001"
argument_list|,
literal|"9d6f1a00a8f3f76e7eb8d4bf80000000"
argument_list|)
block|,
comment|/* [ 32] */
operator|new
name|RandomConstant
argument_list|(
literal|"433bef4314f16a9453cd8fbc00000001"
argument_list|,
literal|"158c62f2b31e496dfd71a97f00000000"
argument_list|)
block|,
comment|/* [ 33] */
operator|new
name|RandomConstant
argument_list|(
literal|"c294b02995ae6738a79b1f7800000001"
argument_list|,
literal|"290e84a2eb15fd1ffae352fe00000000"
argument_list|)
block|,
comment|/* [ 34] */
operator|new
name|RandomConstant
argument_list|(
literal|"913575e0da8b16b14f363ef000000001"
argument_list|,
literal|"e3dc1bfbe991a34ff5c6a5fc00000000"
argument_list|)
block|,
comment|/* [ 35] */
operator|new
name|RandomConstant
argument_list|(
literal|"2f61b9f871cf4e629e6c7de000000001"
argument_list|,
literal|"ddf540d020b9eadfeb8d4bf800000000"
argument_list|)
block|,
comment|/* [ 36] */
operator|new
name|RandomConstant
argument_list|(
literal|"78d26ccbd68320c53cd8fbc000000001"
argument_list|,
literal|"8ee4950177ce66bfd71a97f000000000"
argument_list|)
block|,
comment|/* [ 37] */
operator|new
name|RandomConstant
argument_list|(
literal|"8b7ebd037898518a79b1f78000000001"
argument_list|,
literal|"39e0f787c907117fae352fe000000000"
argument_list|)
block|,
comment|/* [ 38] */
operator|new
name|RandomConstant
argument_list|(
literal|"0b5507b61f78e314f363ef0000000001"
argument_list|,
literal|"659d2522f7b732ff5c6a5fc000000000"
argument_list|)
block|,
comment|/* [ 39] */
operator|new
name|RandomConstant
argument_list|(
literal|"4f884628f812c629e6c7de0000000001"
argument_list|,
literal|"9e8722938612a5feb8d4bf8000000000"
argument_list|)
block|,
comment|/* [ 40] */
operator|new
name|RandomConstant
argument_list|(
literal|"be896744d4a98c53cd8fbc0000000001"
argument_list|,
literal|"e941a65d66b64bfd71a97f0000000000"
argument_list|)
block|,
comment|/* [ 41] */
operator|new
name|RandomConstant
argument_list|(
literal|"daf63a553b6318a79b1f780000000001"
argument_list|,
literal|"7b50d19437b097fae352fe0000000000"
argument_list|)
block|,
comment|/* [ 42] */
operator|new
name|RandomConstant
argument_list|(
literal|"2d7a23d8bf06314f363ef00000000001"
argument_list|,
literal|"59d7b68e18712ff5c6a5fc0000000000"
argument_list|)
block|,
comment|/* [ 43] */
operator|new
name|RandomConstant
argument_list|(
literal|"392b046a9f0c629e6c7de00000000001"
argument_list|,
literal|"4087bab2d5225feb8d4bf80000000000"
argument_list|)
block|,
comment|/* [ 44] */
operator|new
name|RandomConstant
argument_list|(
literal|"eb30fbb9c218c53cd8fbc00000000001"
argument_list|,
literal|"b470abc03b44bfd71a97f00000000000"
argument_list|)
block|,
comment|/* [ 45] */
operator|new
name|RandomConstant
argument_list|(
literal|"b9cdc30594318a79b1f7800000000001"
argument_list|,
literal|"366630eaba897fae352fe00000000000"
argument_list|)
block|,
comment|/* [ 46] */
operator|new
name|RandomConstant
argument_list|(
literal|"014ab453686314f363ef000000000001"
argument_list|,
literal|"a2dfc77e8512ff5c6a5fc00000000000"
argument_list|)
block|,
comment|/* [ 47] */
operator|new
name|RandomConstant
argument_list|(
literal|"395221c7d0c629e6c7de000000000001"
argument_list|,
literal|"1e0d25a14a25feb8d4bf800000000000"
argument_list|)
block|,
comment|/* [ 48] */
operator|new
name|RandomConstant
argument_list|(
literal|"4d972813a18c53cd8fbc000000000001"
argument_list|,
literal|"9d50a5d3944bfd71a97f000000000000"
argument_list|)
block|,
comment|/* [ 49] */
operator|new
name|RandomConstant
argument_list|(
literal|"06f9e2374318a79b1f78000000000001"
argument_list|,
literal|"bf7ab5eb2897fae352fe000000000000"
argument_list|)
block|,
comment|/* [ 50] */
operator|new
name|RandomConstant
argument_list|(
literal|"bd220cae86314f363ef0000000000001"
argument_list|,
literal|"925b14e6512ff5c6a5fc000000000000"
argument_list|)
block|,
comment|/* [ 51] */
operator|new
name|RandomConstant
argument_list|(
literal|"36fd3a5d0c629e6c7de0000000000001"
argument_list|,
literal|"724cce0ca25feb8d4bf8000000000000"
argument_list|)
block|,
comment|/* [ 52] */
operator|new
name|RandomConstant
argument_list|(
literal|"60def8ba18c53cd8fbc0000000000001"
argument_list|,
literal|"1af42d1944bfd71a97f0000000000000"
argument_list|)
block|,
comment|/* [ 53] */
operator|new
name|RandomConstant
argument_list|(
literal|"8d500174318a79b1f780000000000001"
argument_list|,
literal|"0f529e32897fae352fe0000000000000"
argument_list|)
block|,
comment|/* [ 54] */
operator|new
name|RandomConstant
argument_list|(
literal|"48e842e86314f363ef00000000000001"
argument_list|,
literal|"844e4c6512ff5c6a5fc0000000000000"
argument_list|)
block|,
comment|/* [ 55] */
operator|new
name|RandomConstant
argument_list|(
literal|"4af185d0c629e6c7de00000000000001"
argument_list|,
literal|"9f40d8ca25feb8d4bf80000000000000"
argument_list|)
block|,
comment|/* [ 56] */
operator|new
name|RandomConstant
argument_list|(
literal|"7a670ba18c53cd8fbc00000000000001"
argument_list|,
literal|"9912b1944bfd71a97f00000000000000"
argument_list|)
block|,
comment|/* [ 57] */
operator|new
name|RandomConstant
argument_list|(
literal|"86de174318a79b1f7800000000000001"
argument_list|,
literal|"9c69632897fae352fe00000000000000"
argument_list|)
block|,
comment|/* [ 58] */
operator|new
name|RandomConstant
argument_list|(
literal|"55fc2e86314f363ef000000000000001"
argument_list|,
literal|"e1e2c6512ff5c6a5fc00000000000000"
argument_list|)
block|,
comment|/* [ 59] */
operator|new
name|RandomConstant
argument_list|(
literal|"ccf85d0c629e6c7de000000000000001"
argument_list|,
literal|"68058ca25feb8d4bf800000000000000"
argument_list|)
block|,
comment|/* [ 60] */
operator|new
name|RandomConstant
argument_list|(
literal|"1df0ba18c53cd8fbc000000000000001"
argument_list|,
literal|"610b1944bfd71a97f000000000000000"
argument_list|)
block|,
comment|/* [ 61] */
operator|new
name|RandomConstant
argument_list|(
literal|"4be174318a79b1f78000000000000001"
argument_list|,
literal|"061632897fae352fe000000000000000"
argument_list|)
block|,
comment|/* [ 62] */
operator|new
name|RandomConstant
argument_list|(
literal|"d7c2e86314f363ef0000000000000001"
argument_list|,
literal|"1c2c6512ff5c6a5fc000000000000000"
argument_list|)
block|,
comment|/* [ 63] */
operator|new
name|RandomConstant
argument_list|(
literal|"af85d0c629e6c7de0000000000000001"
argument_list|,
literal|"7858ca25feb8d4bf8000000000000000"
argument_list|)
block|,
comment|/* [ 64] */
operator|new
name|RandomConstant
argument_list|(
literal|"5f0ba18c53cd8fbc0000000000000001"
argument_list|,
literal|"f0b1944bfd71a97f0000000000000000"
argument_list|)
block|,
comment|/* [ 65] */
operator|new
name|RandomConstant
argument_list|(
literal|"be174318a79b1f780000000000000001"
argument_list|,
literal|"e1632897fae352fe0000000000000000"
argument_list|)
block|,
comment|/* [ 66] */
operator|new
name|RandomConstant
argument_list|(
literal|"7c2e86314f363ef00000000000000001"
argument_list|,
literal|"c2c6512ff5c6a5fc0000000000000000"
argument_list|)
block|,
comment|/* [ 67] */
operator|new
name|RandomConstant
argument_list|(
literal|"f85d0c629e6c7de00000000000000001"
argument_list|,
literal|"858ca25feb8d4bf80000000000000000"
argument_list|)
block|,
comment|/* [ 68] */
operator|new
name|RandomConstant
argument_list|(
literal|"f0ba18c53cd8fbc00000000000000001"
argument_list|,
literal|"0b1944bfd71a97f00000000000000000"
argument_list|)
block|,
comment|/* [ 69] */
operator|new
name|RandomConstant
argument_list|(
literal|"e174318a79b1f7800000000000000001"
argument_list|,
literal|"1632897fae352fe00000000000000000"
argument_list|)
block|,
comment|/* [ 70] */
operator|new
name|RandomConstant
argument_list|(
literal|"c2e86314f363ef000000000000000001"
argument_list|,
literal|"2c6512ff5c6a5fc00000000000000000"
argument_list|)
block|,
comment|/* [ 71] */
operator|new
name|RandomConstant
argument_list|(
literal|"85d0c629e6c7de000000000000000001"
argument_list|,
literal|"58ca25feb8d4bf800000000000000000"
argument_list|)
block|,
comment|/* [ 72] */
operator|new
name|RandomConstant
argument_list|(
literal|"0ba18c53cd8fbc000000000000000001"
argument_list|,
literal|"b1944bfd71a97f000000000000000000"
argument_list|)
block|,
comment|/* [ 73] */
operator|new
name|RandomConstant
argument_list|(
literal|"174318a79b1f78000000000000000001"
argument_list|,
literal|"632897fae352fe000000000000000000"
argument_list|)
block|,
comment|/* [ 74] */
operator|new
name|RandomConstant
argument_list|(
literal|"2e86314f363ef0000000000000000001"
argument_list|,
literal|"c6512ff5c6a5fc000000000000000000"
argument_list|)
block|,
comment|/* [ 75] */
operator|new
name|RandomConstant
argument_list|(
literal|"5d0c629e6c7de0000000000000000001"
argument_list|,
literal|"8ca25feb8d4bf8000000000000000000"
argument_list|)
block|,
comment|/* [ 76] */
operator|new
name|RandomConstant
argument_list|(
literal|"ba18c53cd8fbc0000000000000000001"
argument_list|,
literal|"1944bfd71a97f0000000000000000000"
argument_list|)
block|,
comment|/* [ 77] */
operator|new
name|RandomConstant
argument_list|(
literal|"74318a79b1f780000000000000000001"
argument_list|,
literal|"32897fae352fe0000000000000000000"
argument_list|)
block|,
comment|/* [ 78] */
operator|new
name|RandomConstant
argument_list|(
literal|"e86314f363ef00000000000000000001"
argument_list|,
literal|"6512ff5c6a5fc0000000000000000000"
argument_list|)
block|,
comment|/* [ 79] */
operator|new
name|RandomConstant
argument_list|(
literal|"d0c629e6c7de00000000000000000001"
argument_list|,
literal|"ca25feb8d4bf80000000000000000000"
argument_list|)
block|,
comment|/* [ 80] */
operator|new
name|RandomConstant
argument_list|(
literal|"a18c53cd8fbc00000000000000000001"
argument_list|,
literal|"944bfd71a97f00000000000000000000"
argument_list|)
block|,
comment|/* [ 81] */
operator|new
name|RandomConstant
argument_list|(
literal|"4318a79b1f7800000000000000000001"
argument_list|,
literal|"2897fae352fe00000000000000000000"
argument_list|)
block|,
comment|/* [ 82] */
operator|new
name|RandomConstant
argument_list|(
literal|"86314f363ef000000000000000000001"
argument_list|,
literal|"512ff5c6a5fc00000000000000000000"
argument_list|)
block|,
comment|/* [ 83] */
operator|new
name|RandomConstant
argument_list|(
literal|"0c629e6c7de000000000000000000001"
argument_list|,
literal|"a25feb8d4bf800000000000000000000"
argument_list|)
block|,
comment|/* [ 84] */
operator|new
name|RandomConstant
argument_list|(
literal|"18c53cd8fbc000000000000000000001"
argument_list|,
literal|"44bfd71a97f000000000000000000000"
argument_list|)
block|,
comment|/* [ 85] */
operator|new
name|RandomConstant
argument_list|(
literal|"318a79b1f78000000000000000000001"
argument_list|,
literal|"897fae352fe000000000000000000000"
argument_list|)
block|,
comment|/* [ 86] */
operator|new
name|RandomConstant
argument_list|(
literal|"6314f363ef0000000000000000000001"
argument_list|,
literal|"12ff5c6a5fc000000000000000000000"
argument_list|)
block|,
comment|/* [ 87] */
operator|new
name|RandomConstant
argument_list|(
literal|"c629e6c7de0000000000000000000001"
argument_list|,
literal|"25feb8d4bf8000000000000000000000"
argument_list|)
block|,
comment|/* [ 88] */
operator|new
name|RandomConstant
argument_list|(
literal|"8c53cd8fbc0000000000000000000001"
argument_list|,
literal|"4bfd71a97f0000000000000000000000"
argument_list|)
block|,
comment|/* [ 89] */
operator|new
name|RandomConstant
argument_list|(
literal|"18a79b1f780000000000000000000001"
argument_list|,
literal|"97fae352fe0000000000000000000000"
argument_list|)
block|,
comment|/* [ 90] */
operator|new
name|RandomConstant
argument_list|(
literal|"314f363ef00000000000000000000001"
argument_list|,
literal|"2ff5c6a5fc0000000000000000000000"
argument_list|)
block|,
comment|/* [ 91] */
operator|new
name|RandomConstant
argument_list|(
literal|"629e6c7de00000000000000000000001"
argument_list|,
literal|"5feb8d4bf80000000000000000000000"
argument_list|)
block|,
comment|/* [ 92] */
operator|new
name|RandomConstant
argument_list|(
literal|"c53cd8fbc00000000000000000000001"
argument_list|,
literal|"bfd71a97f00000000000000000000000"
argument_list|)
block|,
comment|/* [ 93] */
operator|new
name|RandomConstant
argument_list|(
literal|"8a79b1f7800000000000000000000001"
argument_list|,
literal|"7fae352fe00000000000000000000000"
argument_list|)
block|,
comment|/* [ 94] */
operator|new
name|RandomConstant
argument_list|(
literal|"14f363ef000000000000000000000001"
argument_list|,
literal|"ff5c6a5fc00000000000000000000000"
argument_list|)
block|,
comment|/* [ 95] */
operator|new
name|RandomConstant
argument_list|(
literal|"29e6c7de000000000000000000000001"
argument_list|,
literal|"feb8d4bf800000000000000000000000"
argument_list|)
block|,
comment|/* [ 96] */
operator|new
name|RandomConstant
argument_list|(
literal|"53cd8fbc000000000000000000000001"
argument_list|,
literal|"fd71a97f000000000000000000000000"
argument_list|)
block|,
comment|/* [ 97] */
operator|new
name|RandomConstant
argument_list|(
literal|"a79b1f78000000000000000000000001"
argument_list|,
literal|"fae352fe000000000000000000000000"
argument_list|)
block|,
comment|/* [ 98] */
operator|new
name|RandomConstant
argument_list|(
literal|"4f363ef0000000000000000000000001"
argument_list|,
literal|"f5c6a5fc000000000000000000000000"
argument_list|)
block|,
comment|/* [ 99] */
operator|new
name|RandomConstant
argument_list|(
literal|"9e6c7de0000000000000000000000001"
argument_list|,
literal|"eb8d4bf8000000000000000000000000"
argument_list|)
block|,
comment|/* [100] */
operator|new
name|RandomConstant
argument_list|(
literal|"3cd8fbc0000000000000000000000001"
argument_list|,
literal|"d71a97f0000000000000000000000000"
argument_list|)
block|,
comment|/* [101] */
operator|new
name|RandomConstant
argument_list|(
literal|"79b1f780000000000000000000000001"
argument_list|,
literal|"ae352fe0000000000000000000000000"
argument_list|)
block|,
comment|/* [102] */
operator|new
name|RandomConstant
argument_list|(
literal|"f363ef00000000000000000000000001"
argument_list|,
literal|"5c6a5fc0000000000000000000000000"
argument_list|)
block|,
comment|/* [103] */
operator|new
name|RandomConstant
argument_list|(
literal|"e6c7de00000000000000000000000001"
argument_list|,
literal|"b8d4bf80000000000000000000000000"
argument_list|)
block|,
comment|/* [104] */
operator|new
name|RandomConstant
argument_list|(
literal|"cd8fbc00000000000000000000000001"
argument_list|,
literal|"71a97f00000000000000000000000000"
argument_list|)
block|,
comment|/* [105] */
operator|new
name|RandomConstant
argument_list|(
literal|"9b1f7800000000000000000000000001"
argument_list|,
literal|"e352fe00000000000000000000000000"
argument_list|)
block|,
comment|/* [106] */
operator|new
name|RandomConstant
argument_list|(
literal|"363ef000000000000000000000000001"
argument_list|,
literal|"c6a5fc00000000000000000000000000"
argument_list|)
block|,
comment|/* [107] */
operator|new
name|RandomConstant
argument_list|(
literal|"6c7de000000000000000000000000001"
argument_list|,
literal|"8d4bf800000000000000000000000000"
argument_list|)
block|,
comment|/* [108] */
operator|new
name|RandomConstant
argument_list|(
literal|"d8fbc000000000000000000000000001"
argument_list|,
literal|"1a97f000000000000000000000000000"
argument_list|)
block|,
comment|/* [109] */
operator|new
name|RandomConstant
argument_list|(
literal|"b1f78000000000000000000000000001"
argument_list|,
literal|"352fe000000000000000000000000000"
argument_list|)
block|,
comment|/* [110] */
operator|new
name|RandomConstant
argument_list|(
literal|"63ef0000000000000000000000000001"
argument_list|,
literal|"6a5fc000000000000000000000000000"
argument_list|)
block|,
comment|/* [111] */
operator|new
name|RandomConstant
argument_list|(
literal|"c7de0000000000000000000000000001"
argument_list|,
literal|"d4bf8000000000000000000000000000"
argument_list|)
block|,
comment|/* [112] */
operator|new
name|RandomConstant
argument_list|(
literal|"8fbc0000000000000000000000000001"
argument_list|,
literal|"a97f0000000000000000000000000000"
argument_list|)
block|,
comment|/* [113] */
operator|new
name|RandomConstant
argument_list|(
literal|"1f780000000000000000000000000001"
argument_list|,
literal|"52fe0000000000000000000000000000"
argument_list|)
block|,
comment|/* [114] */
operator|new
name|RandomConstant
argument_list|(
literal|"3ef00000000000000000000000000001"
argument_list|,
literal|"a5fc0000000000000000000000000000"
argument_list|)
block|,
comment|/* [115] */
operator|new
name|RandomConstant
argument_list|(
literal|"7de00000000000000000000000000001"
argument_list|,
literal|"4bf80000000000000000000000000000"
argument_list|)
block|,
comment|/* [116] */
operator|new
name|RandomConstant
argument_list|(
literal|"fbc00000000000000000000000000001"
argument_list|,
literal|"97f00000000000000000000000000000"
argument_list|)
block|,
comment|/* [117] */
operator|new
name|RandomConstant
argument_list|(
literal|"f7800000000000000000000000000001"
argument_list|,
literal|"2fe00000000000000000000000000000"
argument_list|)
block|,
comment|/* [118] */
operator|new
name|RandomConstant
argument_list|(
literal|"ef000000000000000000000000000001"
argument_list|,
literal|"5fc00000000000000000000000000000"
argument_list|)
block|,
comment|/* [119] */
operator|new
name|RandomConstant
argument_list|(
literal|"de000000000000000000000000000001"
argument_list|,
literal|"bf800000000000000000000000000000"
argument_list|)
block|,
comment|/* [120] */
operator|new
name|RandomConstant
argument_list|(
literal|"bc000000000000000000000000000001"
argument_list|,
literal|"7f000000000000000000000000000000"
argument_list|)
block|,
comment|/* [121] */
operator|new
name|RandomConstant
argument_list|(
literal|"78000000000000000000000000000001"
argument_list|,
literal|"fe000000000000000000000000000000"
argument_list|)
block|,
comment|/* [122] */
operator|new
name|RandomConstant
argument_list|(
literal|"f0000000000000000000000000000001"
argument_list|,
literal|"fc000000000000000000000000000000"
argument_list|)
block|,
comment|/* [123] */
operator|new
name|RandomConstant
argument_list|(
literal|"e0000000000000000000000000000001"
argument_list|,
literal|"f8000000000000000000000000000000"
argument_list|)
block|,
comment|/* [124] */
operator|new
name|RandomConstant
argument_list|(
literal|"c0000000000000000000000000000001"
argument_list|,
literal|"f0000000000000000000000000000000"
argument_list|)
block|,
comment|/* [125] */
operator|new
name|RandomConstant
argument_list|(
literal|"80000000000000000000000000000001"
argument_list|,
literal|"e0000000000000000000000000000000"
argument_list|)
block|,
comment|/* [126] */
operator|new
name|RandomConstant
argument_list|(
literal|"00000000000000000000000000000001"
argument_list|,
literal|"c0000000000000000000000000000000"
argument_list|)
block|,
comment|/* [127] */
operator|new
name|RandomConstant
argument_list|(
literal|"00000000000000000000000000000001"
argument_list|,
literal|"80000000000000000000000000000000"
argument_list|)
block|}
decl_stmt|;
comment|/**    *  generate the random number that is "advance" steps    *  from an initial random number of 0.  This is done by    *  starting with 0, and then advancing the by the    *  appropriate powers of 2 of the linear congruential    *  generator.    */
DECL|method|skipAhead (Unsigned16 advance)
specifier|public
specifier|static
name|Unsigned16
name|skipAhead
parameter_list|(
name|Unsigned16
name|advance
parameter_list|)
block|{
name|Unsigned16
name|result
init|=
operator|new
name|Unsigned16
argument_list|()
decl_stmt|;
name|long
name|bit_map
decl_stmt|;
name|bit_map
operator|=
name|advance
operator|.
name|getLow8
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|bit_map
operator|!=
literal|0
operator|&&
name|i
operator|<
literal|64
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|(
name|bit_map
operator|&
operator|(
literal|1L
operator|<<
name|i
operator|)
operator|)
operator|!=
literal|0
condition|)
block|{
comment|/* advance random number by f**(2**i) (x)          */
name|result
operator|.
name|multiply
argument_list|(
name|genArray
index|[
name|i
index|]
operator|.
name|a
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|genArray
index|[
name|i
index|]
operator|.
name|c
argument_list|)
expr_stmt|;
name|bit_map
operator|&=
operator|~
operator|(
literal|1L
operator|<<
name|i
operator|)
expr_stmt|;
block|}
block|}
name|bit_map
operator|=
name|advance
operator|.
name|getHigh8
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|bit_map
operator|!=
literal|0
operator|&&
name|i
operator|<
literal|64
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|(
name|bit_map
operator|&
operator|(
literal|1L
operator|<<
name|i
operator|)
operator|)
operator|!=
literal|0
condition|)
block|{
comment|/* advance random number by f**(2**(i + 64)) (x)          */
name|result
operator|.
name|multiply
argument_list|(
name|genArray
index|[
name|i
operator|+
literal|64
index|]
operator|.
name|a
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|genArray
index|[
name|i
operator|+
literal|64
index|]
operator|.
name|c
argument_list|)
expr_stmt|;
name|bit_map
operator|&=
operator|~
operator|(
literal|1L
operator|<<
name|i
operator|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
comment|/**     * Generate the next 16 byte random number.    */
DECL|method|nextRand (Unsigned16 rand)
specifier|public
specifier|static
name|void
name|nextRand
parameter_list|(
name|Unsigned16
name|rand
parameter_list|)
block|{
comment|/* advance the random number forward once using the linear congruential      * generator, and then return the new random number      */
name|rand
operator|.
name|multiply
argument_list|(
name|genArray
index|[
literal|0
index|]
operator|.
name|a
argument_list|)
expr_stmt|;
name|rand
operator|.
name|add
argument_list|(
name|genArray
index|[
literal|0
index|]
operator|.
name|c
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

