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
name|InterfaceStability
import|;
end_import

begin_comment
comment|/**  * Class to contain Lastblock and HdfsFileStatus for the Append operation  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|LastBlockWithStatus
specifier|public
class|class
name|LastBlockWithStatus
block|{
DECL|field|lastBlock
specifier|private
specifier|final
name|LocatedBlock
name|lastBlock
decl_stmt|;
DECL|field|fileStatus
specifier|private
specifier|final
name|HdfsFileStatus
name|fileStatus
decl_stmt|;
DECL|method|LastBlockWithStatus (LocatedBlock lastBlock, HdfsFileStatus fileStatus)
specifier|public
name|LastBlockWithStatus
parameter_list|(
name|LocatedBlock
name|lastBlock
parameter_list|,
name|HdfsFileStatus
name|fileStatus
parameter_list|)
block|{
name|this
operator|.
name|lastBlock
operator|=
name|lastBlock
expr_stmt|;
name|this
operator|.
name|fileStatus
operator|=
name|fileStatus
expr_stmt|;
block|}
DECL|method|getLastBlock ()
specifier|public
name|LocatedBlock
name|getLastBlock
parameter_list|()
block|{
return|return
name|lastBlock
return|;
block|}
DECL|method|getFileStatus ()
specifier|public
name|HdfsFileStatus
name|getFileStatus
parameter_list|()
block|{
return|return
name|fileStatus
return|;
block|}
block|}
end_class

end_unit

