begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.protocol
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocol
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

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|interface|HdfsConstantsClient
specifier|public
interface|interface
name|HdfsConstantsClient
block|{
comment|/**    * Generation stamp of blocks that pre-date the introduction    * of a generation stamp.    */
DECL|field|GRANDFATHER_GENERATION_STAMP
name|long
name|GRANDFATHER_GENERATION_STAMP
init|=
literal|0
decl_stmt|;
comment|/**    * The inode id validation of lease check will be skipped when the request    * uses GRANDFATHER_INODE_ID for backward compatibility.    */
DECL|field|GRANDFATHER_INODE_ID
name|long
name|GRANDFATHER_INODE_ID
init|=
literal|0
decl_stmt|;
DECL|field|BLOCK_STORAGE_POLICY_ID_UNSPECIFIED
name|byte
name|BLOCK_STORAGE_POLICY_ID_UNSPECIFIED
init|=
literal|0
decl_stmt|;
block|}
end_interface

end_unit

