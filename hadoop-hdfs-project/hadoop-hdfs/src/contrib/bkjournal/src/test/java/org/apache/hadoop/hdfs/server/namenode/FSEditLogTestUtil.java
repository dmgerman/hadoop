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
name|IOException
import|;
end_import

begin_comment
comment|/**  * Utilities for testing edit logs  */
end_comment

begin_class
DECL|class|FSEditLogTestUtil
specifier|public
class|class
name|FSEditLogTestUtil
block|{
DECL|method|getNoOpInstance ()
specifier|public
specifier|static
name|FSEditLogOp
name|getNoOpInstance
parameter_list|()
block|{
return|return
name|FSEditLogOp
operator|.
name|LogSegmentOp
operator|.
name|getInstance
argument_list|(
name|FSEditLogOpCodes
operator|.
name|OP_END_LOG_SEGMENT
argument_list|)
return|;
block|}
DECL|method|countTransactionsInStream (EditLogInputStream in)
specifier|public
specifier|static
name|long
name|countTransactionsInStream
parameter_list|(
name|EditLogInputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|FSEditLogLoader
operator|.
name|EditLogValidation
name|validation
init|=
name|FSEditLogLoader
operator|.
name|validateEditLog
argument_list|(
name|in
argument_list|)
decl_stmt|;
return|return
name|validation
operator|.
name|getNumTransactions
argument_list|()
return|;
block|}
block|}
end_class

end_unit

