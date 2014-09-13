begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred.nativetask
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|nativetask
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceAudience
import|;
end_import

begin_enum
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|enum|DataChannel
specifier|public
enum|enum
name|DataChannel
block|{
comment|/**    * We will only read data from this channel    */
DECL|enumConstant|IN
name|IN
block|,
comment|/**    * We will only write data from this channel    */
DECL|enumConstant|OUT
name|OUT
block|,
comment|/**    * We will do both read and write for this channel    */
DECL|enumConstant|INOUT
name|INOUT
block|,
comment|/**    * There is no data exchange    */
DECL|enumConstant|NONE
name|NONE
block|}
end_enum

end_unit

