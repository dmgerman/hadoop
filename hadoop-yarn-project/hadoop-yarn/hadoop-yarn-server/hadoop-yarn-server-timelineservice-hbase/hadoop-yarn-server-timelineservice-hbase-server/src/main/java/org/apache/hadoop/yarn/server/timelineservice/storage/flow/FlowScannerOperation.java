begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.storage.flow
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
name|storage
operator|.
name|flow
package|;
end_package

begin_comment
comment|/**  * Identifies the scanner operation on the {@link FlowRunTable}.  */
end_comment

begin_enum
DECL|enum|FlowScannerOperation
specifier|public
enum|enum
name|FlowScannerOperation
block|{
comment|/**    * If the scanner is opened for reading    * during preGet or preScan.    */
DECL|enumConstant|READ
name|READ
block|,
comment|/**    * If the scanner is opened during preFlush.    */
DECL|enumConstant|FLUSH
name|FLUSH
block|,
comment|/**    * If the scanner is opened during minor Compaction.    */
DECL|enumConstant|MINOR_COMPACTION
name|MINOR_COMPACTION
block|,
comment|/**    * If the scanner is opened during major Compaction.    */
DECL|enumConstant|MAJOR_COMPACTION
name|MAJOR_COMPACTION
block|}
end_enum

end_unit

