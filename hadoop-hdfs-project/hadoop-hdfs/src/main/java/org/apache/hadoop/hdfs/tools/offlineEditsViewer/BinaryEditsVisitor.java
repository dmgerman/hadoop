begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.tools.offlineEditsViewer
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|tools
operator|.
name|offlineEditsViewer
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
name|server
operator|.
name|namenode
operator|.
name|FSEditLogOp
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
name|server
operator|.
name|namenode
operator|.
name|EditLogFileOutputStream
import|;
end_import

begin_comment
comment|/**  * BinaryEditsVisitor implements a binary EditsVisitor  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|BinaryEditsVisitor
specifier|public
class|class
name|BinaryEditsVisitor
implements|implements
name|OfflineEditsVisitor
block|{
DECL|field|elfos
specifier|final
specifier|private
name|EditLogFileOutputStream
name|elfos
decl_stmt|;
comment|/**    * Create a processor that writes to a given file    *    * @param filename Name of file to write output to    */
DECL|method|BinaryEditsVisitor (String outputName)
specifier|public
name|BinaryEditsVisitor
parameter_list|(
name|String
name|outputName
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|elfos
operator|=
operator|new
name|EditLogFileOutputStream
argument_list|(
operator|new
name|File
argument_list|(
name|outputName
argument_list|)
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|elfos
operator|.
name|create
argument_list|()
expr_stmt|;
block|}
comment|/**    * Start the visitor (initialization)    */
annotation|@
name|Override
DECL|method|start (int version)
specifier|public
name|void
name|start
parameter_list|(
name|int
name|version
parameter_list|)
throws|throws
name|IOException
block|{   }
comment|/**    * Finish the visitor    */
annotation|@
name|Override
DECL|method|close (Throwable error)
specifier|public
name|void
name|close
parameter_list|(
name|Throwable
name|error
parameter_list|)
throws|throws
name|IOException
block|{
name|elfos
operator|.
name|setReadyToFlush
argument_list|()
expr_stmt|;
name|elfos
operator|.
name|flushAndSync
argument_list|()
expr_stmt|;
name|elfos
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|visitOp (FSEditLogOp op)
specifier|public
name|void
name|visitOp
parameter_list|(
name|FSEditLogOp
name|op
parameter_list|)
throws|throws
name|IOException
block|{
name|elfos
operator|.
name|write
argument_list|(
name|op
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

