begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.reader
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|timelineservice
operator|.
name|reader
package|;
end_package

begin_comment
comment|/**  * Set of constants used while parsing filter expressions.  */
end_comment

begin_class
DECL|class|TimelineParseConstants
specifier|final
class|class
name|TimelineParseConstants
block|{
DECL|method|TimelineParseConstants ()
specifier|private
name|TimelineParseConstants
parameter_list|()
block|{   }
DECL|field|COMMA_DELIMITER
specifier|static
specifier|final
name|String
name|COMMA_DELIMITER
init|=
literal|","
decl_stmt|;
DECL|field|COLON_DELIMITER
specifier|static
specifier|final
name|String
name|COLON_DELIMITER
init|=
literal|":"
decl_stmt|;
DECL|field|NOT_CHAR
specifier|static
specifier|final
name|char
name|NOT_CHAR
init|=
literal|'!'
decl_stmt|;
DECL|field|SPACE_CHAR
specifier|static
specifier|final
name|char
name|SPACE_CHAR
init|=
literal|' '
decl_stmt|;
DECL|field|OPENING_BRACKET_CHAR
specifier|static
specifier|final
name|char
name|OPENING_BRACKET_CHAR
init|=
literal|'('
decl_stmt|;
DECL|field|CLOSING_BRACKET_CHAR
specifier|static
specifier|final
name|char
name|CLOSING_BRACKET_CHAR
init|=
literal|')'
decl_stmt|;
DECL|field|COMMA_CHAR
specifier|static
specifier|final
name|char
name|COMMA_CHAR
init|=
literal|','
decl_stmt|;
block|}
end_class

end_unit

