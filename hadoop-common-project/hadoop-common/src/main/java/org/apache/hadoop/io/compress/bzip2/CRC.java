begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  Licensed to the Apache Software Foundation (ASF) under one or more  *  contributor license agreements.  See the NOTICE file distributed with  *  this work for additional information regarding copyright ownership.  *  The ASF licenses this file to You under the Apache License, Version 2.0  *  (the "License"); you may not use this file except in compliance with  *  the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  *  */
end_comment

begin_comment
comment|/*  * This package is based on the work done by Keiron Liddle, Aftex Software  *<keiron@aftexsw.com> to whom the Ant project is very grateful for his  * great code.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.io.compress.bzip2
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|compress
operator|.
name|bzip2
package|;
end_package

begin_comment
comment|/**  * A simple class the hold and calculate the CRC for sanity checking of the  * data.  *  */
end_comment

begin_class
DECL|class|CRC
specifier|final
class|class
name|CRC
block|{
DECL|field|crc32Table
specifier|static
specifier|final
name|int
name|crc32Table
index|[]
init|=
block|{
literal|0x00000000
block|,
literal|0x04c11db7
block|,
literal|0x09823b6e
block|,
literal|0x0d4326d9
block|,
literal|0x130476dc
block|,
literal|0x17c56b6b
block|,
literal|0x1a864db2
block|,
literal|0x1e475005
block|,
literal|0x2608edb8
block|,
literal|0x22c9f00f
block|,
literal|0x2f8ad6d6
block|,
literal|0x2b4bcb61
block|,
literal|0x350c9b64
block|,
literal|0x31cd86d3
block|,
literal|0x3c8ea00a
block|,
literal|0x384fbdbd
block|,
literal|0x4c11db70
block|,
literal|0x48d0c6c7
block|,
literal|0x4593e01e
block|,
literal|0x4152fda9
block|,
literal|0x5f15adac
block|,
literal|0x5bd4b01b
block|,
literal|0x569796c2
block|,
literal|0x52568b75
block|,
literal|0x6a1936c8
block|,
literal|0x6ed82b7f
block|,
literal|0x639b0da6
block|,
literal|0x675a1011
block|,
literal|0x791d4014
block|,
literal|0x7ddc5da3
block|,
literal|0x709f7b7a
block|,
literal|0x745e66cd
block|,
literal|0x9823b6e0
block|,
literal|0x9ce2ab57
block|,
literal|0x91a18d8e
block|,
literal|0x95609039
block|,
literal|0x8b27c03c
block|,
literal|0x8fe6dd8b
block|,
literal|0x82a5fb52
block|,
literal|0x8664e6e5
block|,
literal|0xbe2b5b58
block|,
literal|0xbaea46ef
block|,
literal|0xb7a96036
block|,
literal|0xb3687d81
block|,
literal|0xad2f2d84
block|,
literal|0xa9ee3033
block|,
literal|0xa4ad16ea
block|,
literal|0xa06c0b5d
block|,
literal|0xd4326d90
block|,
literal|0xd0f37027
block|,
literal|0xddb056fe
block|,
literal|0xd9714b49
block|,
literal|0xc7361b4c
block|,
literal|0xc3f706fb
block|,
literal|0xceb42022
block|,
literal|0xca753d95
block|,
literal|0xf23a8028
block|,
literal|0xf6fb9d9f
block|,
literal|0xfbb8bb46
block|,
literal|0xff79a6f1
block|,
literal|0xe13ef6f4
block|,
literal|0xe5ffeb43
block|,
literal|0xe8bccd9a
block|,
literal|0xec7dd02d
block|,
literal|0x34867077
block|,
literal|0x30476dc0
block|,
literal|0x3d044b19
block|,
literal|0x39c556ae
block|,
literal|0x278206ab
block|,
literal|0x23431b1c
block|,
literal|0x2e003dc5
block|,
literal|0x2ac12072
block|,
literal|0x128e9dcf
block|,
literal|0x164f8078
block|,
literal|0x1b0ca6a1
block|,
literal|0x1fcdbb16
block|,
literal|0x018aeb13
block|,
literal|0x054bf6a4
block|,
literal|0x0808d07d
block|,
literal|0x0cc9cdca
block|,
literal|0x7897ab07
block|,
literal|0x7c56b6b0
block|,
literal|0x71159069
block|,
literal|0x75d48dde
block|,
literal|0x6b93dddb
block|,
literal|0x6f52c06c
block|,
literal|0x6211e6b5
block|,
literal|0x66d0fb02
block|,
literal|0x5e9f46bf
block|,
literal|0x5a5e5b08
block|,
literal|0x571d7dd1
block|,
literal|0x53dc6066
block|,
literal|0x4d9b3063
block|,
literal|0x495a2dd4
block|,
literal|0x44190b0d
block|,
literal|0x40d816ba
block|,
literal|0xaca5c697
block|,
literal|0xa864db20
block|,
literal|0xa527fdf9
block|,
literal|0xa1e6e04e
block|,
literal|0xbfa1b04b
block|,
literal|0xbb60adfc
block|,
literal|0xb6238b25
block|,
literal|0xb2e29692
block|,
literal|0x8aad2b2f
block|,
literal|0x8e6c3698
block|,
literal|0x832f1041
block|,
literal|0x87ee0df6
block|,
literal|0x99a95df3
block|,
literal|0x9d684044
block|,
literal|0x902b669d
block|,
literal|0x94ea7b2a
block|,
literal|0xe0b41de7
block|,
literal|0xe4750050
block|,
literal|0xe9362689
block|,
literal|0xedf73b3e
block|,
literal|0xf3b06b3b
block|,
literal|0xf771768c
block|,
literal|0xfa325055
block|,
literal|0xfef34de2
block|,
literal|0xc6bcf05f
block|,
literal|0xc27dede8
block|,
literal|0xcf3ecb31
block|,
literal|0xcbffd686
block|,
literal|0xd5b88683
block|,
literal|0xd1799b34
block|,
literal|0xdc3abded
block|,
literal|0xd8fba05a
block|,
literal|0x690ce0ee
block|,
literal|0x6dcdfd59
block|,
literal|0x608edb80
block|,
literal|0x644fc637
block|,
literal|0x7a089632
block|,
literal|0x7ec98b85
block|,
literal|0x738aad5c
block|,
literal|0x774bb0eb
block|,
literal|0x4f040d56
block|,
literal|0x4bc510e1
block|,
literal|0x46863638
block|,
literal|0x42472b8f
block|,
literal|0x5c007b8a
block|,
literal|0x58c1663d
block|,
literal|0x558240e4
block|,
literal|0x51435d53
block|,
literal|0x251d3b9e
block|,
literal|0x21dc2629
block|,
literal|0x2c9f00f0
block|,
literal|0x285e1d47
block|,
literal|0x36194d42
block|,
literal|0x32d850f5
block|,
literal|0x3f9b762c
block|,
literal|0x3b5a6b9b
block|,
literal|0x0315d626
block|,
literal|0x07d4cb91
block|,
literal|0x0a97ed48
block|,
literal|0x0e56f0ff
block|,
literal|0x1011a0fa
block|,
literal|0x14d0bd4d
block|,
literal|0x19939b94
block|,
literal|0x1d528623
block|,
literal|0xf12f560e
block|,
literal|0xf5ee4bb9
block|,
literal|0xf8ad6d60
block|,
literal|0xfc6c70d7
block|,
literal|0xe22b20d2
block|,
literal|0xe6ea3d65
block|,
literal|0xeba91bbc
block|,
literal|0xef68060b
block|,
literal|0xd727bbb6
block|,
literal|0xd3e6a601
block|,
literal|0xdea580d8
block|,
literal|0xda649d6f
block|,
literal|0xc423cd6a
block|,
literal|0xc0e2d0dd
block|,
literal|0xcda1f604
block|,
literal|0xc960ebb3
block|,
literal|0xbd3e8d7e
block|,
literal|0xb9ff90c9
block|,
literal|0xb4bcb610
block|,
literal|0xb07daba7
block|,
literal|0xae3afba2
block|,
literal|0xaafbe615
block|,
literal|0xa7b8c0cc
block|,
literal|0xa379dd7b
block|,
literal|0x9b3660c6
block|,
literal|0x9ff77d71
block|,
literal|0x92b45ba8
block|,
literal|0x9675461f
block|,
literal|0x8832161a
block|,
literal|0x8cf30bad
block|,
literal|0x81b02d74
block|,
literal|0x857130c3
block|,
literal|0x5d8a9099
block|,
literal|0x594b8d2e
block|,
literal|0x5408abf7
block|,
literal|0x50c9b640
block|,
literal|0x4e8ee645
block|,
literal|0x4a4ffbf2
block|,
literal|0x470cdd2b
block|,
literal|0x43cdc09c
block|,
literal|0x7b827d21
block|,
literal|0x7f436096
block|,
literal|0x7200464f
block|,
literal|0x76c15bf8
block|,
literal|0x68860bfd
block|,
literal|0x6c47164a
block|,
literal|0x61043093
block|,
literal|0x65c52d24
block|,
literal|0x119b4be9
block|,
literal|0x155a565e
block|,
literal|0x18197087
block|,
literal|0x1cd86d30
block|,
literal|0x029f3d35
block|,
literal|0x065e2082
block|,
literal|0x0b1d065b
block|,
literal|0x0fdc1bec
block|,
literal|0x3793a651
block|,
literal|0x3352bbe6
block|,
literal|0x3e119d3f
block|,
literal|0x3ad08088
block|,
literal|0x2497d08d
block|,
literal|0x2056cd3a
block|,
literal|0x2d15ebe3
block|,
literal|0x29d4f654
block|,
literal|0xc5a92679
block|,
literal|0xc1683bce
block|,
literal|0xcc2b1d17
block|,
literal|0xc8ea00a0
block|,
literal|0xd6ad50a5
block|,
literal|0xd26c4d12
block|,
literal|0xdf2f6bcb
block|,
literal|0xdbee767c
block|,
literal|0xe3a1cbc1
block|,
literal|0xe760d676
block|,
literal|0xea23f0af
block|,
literal|0xeee2ed18
block|,
literal|0xf0a5bd1d
block|,
literal|0xf464a0aa
block|,
literal|0xf9278673
block|,
literal|0xfde69bc4
block|,
literal|0x89b8fd09
block|,
literal|0x8d79e0be
block|,
literal|0x803ac667
block|,
literal|0x84fbdbd0
block|,
literal|0x9abc8bd5
block|,
literal|0x9e7d9662
block|,
literal|0x933eb0bb
block|,
literal|0x97ffad0c
block|,
literal|0xafb010b1
block|,
literal|0xab710d06
block|,
literal|0xa6322bdf
block|,
literal|0xa2f33668
block|,
literal|0xbcb4666d
block|,
literal|0xb8757bda
block|,
literal|0xb5365d03
block|,
literal|0xb1f740b4
block|}
decl_stmt|;
DECL|method|CRC ()
name|CRC
parameter_list|()
block|{
name|initialiseCRC
argument_list|()
expr_stmt|;
block|}
DECL|method|initialiseCRC ()
name|void
name|initialiseCRC
parameter_list|()
block|{
name|globalCrc
operator|=
literal|0xffffffff
expr_stmt|;
block|}
DECL|method|getFinalCRC ()
name|int
name|getFinalCRC
parameter_list|()
block|{
return|return
operator|~
name|globalCrc
return|;
block|}
DECL|method|getGlobalCRC ()
name|int
name|getGlobalCRC
parameter_list|()
block|{
return|return
name|globalCrc
return|;
block|}
DECL|method|setGlobalCRC (int newCrc)
name|void
name|setGlobalCRC
parameter_list|(
name|int
name|newCrc
parameter_list|)
block|{
name|globalCrc
operator|=
name|newCrc
expr_stmt|;
block|}
DECL|method|updateCRC (int inCh)
name|void
name|updateCRC
parameter_list|(
name|int
name|inCh
parameter_list|)
block|{
name|int
name|temp
init|=
operator|(
name|globalCrc
operator|>>
literal|24
operator|)
operator|^
name|inCh
decl_stmt|;
if|if
condition|(
name|temp
operator|<
literal|0
condition|)
block|{
name|temp
operator|=
literal|256
operator|+
name|temp
expr_stmt|;
block|}
name|globalCrc
operator|=
operator|(
name|globalCrc
operator|<<
literal|8
operator|)
operator|^
name|CRC
operator|.
name|crc32Table
index|[
name|temp
index|]
expr_stmt|;
block|}
DECL|method|updateCRC (int inCh, int repeat)
name|void
name|updateCRC
parameter_list|(
name|int
name|inCh
parameter_list|,
name|int
name|repeat
parameter_list|)
block|{
name|int
name|globalCrcShadow
init|=
name|this
operator|.
name|globalCrc
decl_stmt|;
while|while
condition|(
name|repeat
operator|--
operator|>
literal|0
condition|)
block|{
name|int
name|temp
init|=
operator|(
name|globalCrcShadow
operator|>>
literal|24
operator|)
operator|^
name|inCh
decl_stmt|;
name|globalCrcShadow
operator|=
operator|(
name|globalCrcShadow
operator|<<
literal|8
operator|)
operator|^
name|crc32Table
index|[
operator|(
name|temp
operator|>=
literal|0
operator|)
condition|?
name|temp
else|:
operator|(
name|temp
operator|+
literal|256
operator|)
index|]
expr_stmt|;
block|}
name|this
operator|.
name|globalCrc
operator|=
name|globalCrcShadow
expr_stmt|;
block|}
DECL|field|globalCrc
name|int
name|globalCrc
decl_stmt|;
block|}
end_class

end_unit

