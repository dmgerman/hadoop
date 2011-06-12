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
name|BufferedInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInputStream
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
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_comment
comment|/**  * A generic abstract class to support reading edits log data from   * persistent storage.  *   * It should stream bytes from the storage exactly as they were written  * into the #{@link EditLogOutputStream}.  */
end_comment

begin_class
DECL|class|EditLogInputStream
specifier|abstract
class|class
name|EditLogInputStream
extends|extends
name|InputStream
implements|implements
name|JournalStream
block|{
comment|/** {@inheritDoc} */
DECL|method|available ()
specifier|public
specifier|abstract
name|int
name|available
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** {@inheritDoc} */
DECL|method|read ()
specifier|public
specifier|abstract
name|int
name|read
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** {@inheritDoc} */
DECL|method|read (byte[] b, int off, int len)
specifier|public
specifier|abstract
name|int
name|read
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** {@inheritDoc} */
DECL|method|close ()
specifier|public
specifier|abstract
name|void
name|close
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Return the size of the current edits log.    */
DECL|method|length ()
specifier|abstract
name|long
name|length
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Return DataInputStream based on this edit stream.    */
DECL|method|getDataInputStream ()
name|DataInputStream
name|getDataInputStream
parameter_list|()
block|{
return|return
operator|new
name|DataInputStream
argument_list|(
operator|new
name|BufferedInputStream
argument_list|(
name|this
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

