begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|namenode
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * Utility class to faciliate some fault injection tests for the checkpointing  * process.  */
end_comment

begin_class
DECL|class|CheckpointFaultInjector
specifier|public
class|class
name|CheckpointFaultInjector
block|{
DECL|field|instance
specifier|public
specifier|static
name|CheckpointFaultInjector
name|instance
init|=
operator|new
name|CheckpointFaultInjector
argument_list|()
decl_stmt|;
DECL|method|getInstance ()
specifier|public
specifier|static
name|CheckpointFaultInjector
name|getInstance
parameter_list|()
block|{
return|return
name|instance
return|;
block|}
DECL|method|set (CheckpointFaultInjector instance)
specifier|public
specifier|static
name|void
name|set
parameter_list|(
name|CheckpointFaultInjector
name|instance
parameter_list|)
block|{
name|CheckpointFaultInjector
operator|.
name|instance
operator|=
name|instance
expr_stmt|;
block|}
DECL|method|beforeGetImageSetsHeaders ()
specifier|public
name|void
name|beforeGetImageSetsHeaders
parameter_list|()
throws|throws
name|IOException
block|{}
DECL|method|afterSecondaryCallsRollEditLog ()
specifier|public
name|void
name|afterSecondaryCallsRollEditLog
parameter_list|()
throws|throws
name|IOException
block|{}
DECL|method|duringMerge ()
specifier|public
name|void
name|duringMerge
parameter_list|()
throws|throws
name|IOException
block|{}
DECL|method|afterSecondaryUploadsNewImage ()
specifier|public
name|void
name|afterSecondaryUploadsNewImage
parameter_list|()
throws|throws
name|IOException
block|{}
DECL|method|aboutToSendFile (File localfile)
specifier|public
name|void
name|aboutToSendFile
parameter_list|(
name|File
name|localfile
parameter_list|)
throws|throws
name|IOException
block|{}
DECL|method|shouldSendShortFile (File localfile)
specifier|public
name|boolean
name|shouldSendShortFile
parameter_list|(
name|File
name|localfile
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
DECL|method|shouldCorruptAByte (File localfile)
specifier|public
name|boolean
name|shouldCorruptAByte
parameter_list|(
name|File
name|localfile
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
DECL|method|afterMD5Rename ()
specifier|public
name|void
name|afterMD5Rename
parameter_list|()
throws|throws
name|IOException
block|{}
DECL|method|beforeEditsRename ()
specifier|public
name|void
name|beforeEditsRename
parameter_list|()
throws|throws
name|IOException
block|{}
DECL|method|duringUploadInProgess ()
specifier|public
name|void
name|duringUploadInProgess
parameter_list|()
throws|throws
name|InterruptedException
throws|,
name|IOException
block|{   }
block|}
end_class

end_unit

