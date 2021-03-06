begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
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
name|conf
operator|.
name|Configuration
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
name|hdfs
operator|.
name|client
operator|.
name|HdfsClientConfigKeys
import|;
end_import

begin_comment
comment|/**  * End-to-end tests for COMPOSITE_CRC combine mode.  */
end_comment

begin_class
DECL|class|TestFileChecksumCompositeCrc
specifier|public
class|class
name|TestFileChecksumCompositeCrc
extends|extends
name|TestFileChecksum
block|{
annotation|@
name|Override
DECL|method|customizeConf (Configuration conf)
specifier|protected
name|void
name|customizeConf
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|conf
operator|.
name|set
argument_list|(
name|HdfsClientConfigKeys
operator|.
name|DFS_CHECKSUM_COMBINE_MODE_KEY
argument_list|,
literal|"COMPOSITE_CRC"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|expectComparableStripedAndReplicatedFiles ()
specifier|protected
name|boolean
name|expectComparableStripedAndReplicatedFiles
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|expectComparableDifferentBlockSizeReplicatedFiles ()
specifier|protected
name|boolean
name|expectComparableDifferentBlockSizeReplicatedFiles
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|expectSupportForSingleFileMixedBytesPerChecksum ()
specifier|protected
name|boolean
name|expectSupportForSingleFileMixedBytesPerChecksum
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

