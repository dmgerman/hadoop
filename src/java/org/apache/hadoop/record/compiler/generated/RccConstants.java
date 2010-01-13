begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/* Generated By:JavaCC: Do not edit this line. RccConstants.java */
end_comment

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.record.compiler.generated
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|record
operator|.
name|compiler
operator|.
name|generated
package|;
end_package

begin_comment
comment|/**  * @deprecated Replaced by<a href="http://hadoop.apache.org/avro/">Avro</a>.  */
end_comment

begin_interface
annotation|@
name|Deprecated
DECL|interface|RccConstants
specifier|public
interface|interface
name|RccConstants
block|{
DECL|field|EOF
name|int
name|EOF
init|=
literal|0
decl_stmt|;
DECL|field|MODULE_TKN
name|int
name|MODULE_TKN
init|=
literal|11
decl_stmt|;
DECL|field|RECORD_TKN
name|int
name|RECORD_TKN
init|=
literal|12
decl_stmt|;
DECL|field|INCLUDE_TKN
name|int
name|INCLUDE_TKN
init|=
literal|13
decl_stmt|;
DECL|field|BYTE_TKN
name|int
name|BYTE_TKN
init|=
literal|14
decl_stmt|;
DECL|field|BOOLEAN_TKN
name|int
name|BOOLEAN_TKN
init|=
literal|15
decl_stmt|;
DECL|field|INT_TKN
name|int
name|INT_TKN
init|=
literal|16
decl_stmt|;
DECL|field|LONG_TKN
name|int
name|LONG_TKN
init|=
literal|17
decl_stmt|;
DECL|field|FLOAT_TKN
name|int
name|FLOAT_TKN
init|=
literal|18
decl_stmt|;
DECL|field|DOUBLE_TKN
name|int
name|DOUBLE_TKN
init|=
literal|19
decl_stmt|;
DECL|field|USTRING_TKN
name|int
name|USTRING_TKN
init|=
literal|20
decl_stmt|;
DECL|field|BUFFER_TKN
name|int
name|BUFFER_TKN
init|=
literal|21
decl_stmt|;
DECL|field|VECTOR_TKN
name|int
name|VECTOR_TKN
init|=
literal|22
decl_stmt|;
DECL|field|MAP_TKN
name|int
name|MAP_TKN
init|=
literal|23
decl_stmt|;
DECL|field|LBRACE_TKN
name|int
name|LBRACE_TKN
init|=
literal|24
decl_stmt|;
DECL|field|RBRACE_TKN
name|int
name|RBRACE_TKN
init|=
literal|25
decl_stmt|;
DECL|field|LT_TKN
name|int
name|LT_TKN
init|=
literal|26
decl_stmt|;
DECL|field|GT_TKN
name|int
name|GT_TKN
init|=
literal|27
decl_stmt|;
DECL|field|SEMICOLON_TKN
name|int
name|SEMICOLON_TKN
init|=
literal|28
decl_stmt|;
DECL|field|COMMA_TKN
name|int
name|COMMA_TKN
init|=
literal|29
decl_stmt|;
DECL|field|DOT_TKN
name|int
name|DOT_TKN
init|=
literal|30
decl_stmt|;
DECL|field|CSTRING_TKN
name|int
name|CSTRING_TKN
init|=
literal|31
decl_stmt|;
DECL|field|IDENT_TKN
name|int
name|IDENT_TKN
init|=
literal|32
decl_stmt|;
DECL|field|DEFAULT
name|int
name|DEFAULT
init|=
literal|0
decl_stmt|;
DECL|field|WithinOneLineComment
name|int
name|WithinOneLineComment
init|=
literal|1
decl_stmt|;
DECL|field|WithinMultiLineComment
name|int
name|WithinMultiLineComment
init|=
literal|2
decl_stmt|;
DECL|field|tokenImage
name|String
index|[]
name|tokenImage
init|=
block|{
literal|"<EOF>"
block|,
literal|"\" \""
block|,
literal|"\"\\t\""
block|,
literal|"\"\\n\""
block|,
literal|"\"\\r\""
block|,
literal|"\"//\""
block|,
literal|"<token of kind 6>"
block|,
literal|"<token of kind 7>"
block|,
literal|"\"/*\""
block|,
literal|"\"*/\""
block|,
literal|"<token of kind 10>"
block|,
literal|"\"module\""
block|,
literal|"\"class\""
block|,
literal|"\"include\""
block|,
literal|"\"byte\""
block|,
literal|"\"boolean\""
block|,
literal|"\"int\""
block|,
literal|"\"long\""
block|,
literal|"\"float\""
block|,
literal|"\"double\""
block|,
literal|"\"ustring\""
block|,
literal|"\"buffer\""
block|,
literal|"\"vector\""
block|,
literal|"\"map\""
block|,
literal|"\"{\""
block|,
literal|"\"}\""
block|,
literal|"\"<\""
block|,
literal|"\">\""
block|,
literal|"\";\""
block|,
literal|"\",\""
block|,
literal|"\".\""
block|,
literal|"<CSTRING_TKN>"
block|,
literal|"<IDENT_TKN>"
block|,   }
decl_stmt|;
block|}
end_interface

end_unit

