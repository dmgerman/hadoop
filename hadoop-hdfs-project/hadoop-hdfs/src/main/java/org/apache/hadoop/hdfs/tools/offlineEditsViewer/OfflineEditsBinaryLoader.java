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
name|IOException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|tools
operator|.
name|offlineEditsViewer
operator|.
name|OfflineEditsViewer
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
name|EditLogInputStream
import|;
end_import

begin_comment
comment|/**  * OfflineEditsBinaryLoader loads edits from a binary edits file  */
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
DECL|class|OfflineEditsBinaryLoader
class|class
name|OfflineEditsBinaryLoader
implements|implements
name|OfflineEditsLoader
block|{
DECL|field|visitor
specifier|private
name|OfflineEditsVisitor
name|visitor
decl_stmt|;
DECL|field|inputStream
specifier|private
name|EditLogInputStream
name|inputStream
decl_stmt|;
DECL|field|fixTxIds
specifier|private
specifier|final
name|boolean
name|fixTxIds
decl_stmt|;
DECL|field|recoveryMode
specifier|private
specifier|final
name|boolean
name|recoveryMode
decl_stmt|;
DECL|field|nextTxId
specifier|private
name|long
name|nextTxId
decl_stmt|;
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|OfflineEditsBinaryLoader
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
comment|/**    * Constructor    */
DECL|method|OfflineEditsBinaryLoader (OfflineEditsVisitor visitor, EditLogInputStream inputStream, OfflineEditsViewer.Flags flags)
specifier|public
name|OfflineEditsBinaryLoader
parameter_list|(
name|OfflineEditsVisitor
name|visitor
parameter_list|,
name|EditLogInputStream
name|inputStream
parameter_list|,
name|OfflineEditsViewer
operator|.
name|Flags
name|flags
parameter_list|)
block|{
name|this
operator|.
name|visitor
operator|=
name|visitor
expr_stmt|;
name|this
operator|.
name|inputStream
operator|=
name|inputStream
expr_stmt|;
name|this
operator|.
name|fixTxIds
operator|=
name|flags
operator|.
name|getFixTxIds
argument_list|()
expr_stmt|;
name|this
operator|.
name|recoveryMode
operator|=
name|flags
operator|.
name|getRecoveryMode
argument_list|()
expr_stmt|;
name|this
operator|.
name|nextTxId
operator|=
operator|-
literal|1
expr_stmt|;
block|}
comment|/**    * Loads edits file, uses visitor to process all elements    */
DECL|method|loadEdits ()
specifier|public
name|void
name|loadEdits
parameter_list|()
throws|throws
name|IOException
block|{
name|visitor
operator|.
name|start
argument_list|(
name|inputStream
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
try|try
block|{
name|FSEditLogOp
name|op
init|=
name|inputStream
operator|.
name|readOp
argument_list|()
decl_stmt|;
if|if
condition|(
name|op
operator|==
literal|null
condition|)
break|break;
if|if
condition|(
name|fixTxIds
condition|)
block|{
if|if
condition|(
name|nextTxId
operator|<=
literal|0
condition|)
block|{
name|nextTxId
operator|=
name|op
operator|.
name|getTransactionId
argument_list|()
expr_stmt|;
if|if
condition|(
name|nextTxId
operator|<=
literal|0
condition|)
block|{
name|nextTxId
operator|=
literal|1
expr_stmt|;
block|}
block|}
name|op
operator|.
name|setTransactionId
argument_list|(
name|nextTxId
argument_list|)
expr_stmt|;
name|nextTxId
operator|++
expr_stmt|;
block|}
name|visitor
operator|.
name|visitOp
argument_list|(
name|op
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
if|if
condition|(
operator|!
name|recoveryMode
condition|)
block|{
comment|// Tell the visitor to clean up, then re-throw the exception
name|visitor
operator|.
name|close
argument_list|(
name|e
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
name|LOG
operator|.
name|error
argument_list|(
literal|"Got IOException while reading stream!  Resyncing."
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|inputStream
operator|.
name|resync
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
if|if
condition|(
operator|!
name|recoveryMode
condition|)
block|{
comment|// Tell the visitor to clean up, then re-throw the exception
name|visitor
operator|.
name|close
argument_list|(
name|e
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
name|LOG
operator|.
name|error
argument_list|(
literal|"Got RuntimeException while reading stream!  Resyncing."
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|inputStream
operator|.
name|resync
argument_list|()
expr_stmt|;
block|}
block|}
name|visitor
operator|.
name|close
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

