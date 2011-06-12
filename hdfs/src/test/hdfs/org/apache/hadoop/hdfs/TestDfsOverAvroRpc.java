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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/** Test for simple signs of life using Avro RPC.  Not an exhaustive test  * yet, just enough to catch fundamental problems using Avro reflection to  * infer namenode RPC protocols. */
end_comment

begin_class
DECL|class|TestDfsOverAvroRpc
specifier|public
class|class
name|TestDfsOverAvroRpc
extends|extends
name|TestLocalDFS
block|{
DECL|method|testWorkingDirectory ()
specifier|public
name|void
name|testWorkingDirectory
parameter_list|()
throws|throws
name|IOException
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"hdfs.rpc.engine"
argument_list|,
literal|"org.apache.hadoop.ipc.AvroRpcEngine"
argument_list|)
expr_stmt|;
name|super
operator|.
name|testWorkingDirectory
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

