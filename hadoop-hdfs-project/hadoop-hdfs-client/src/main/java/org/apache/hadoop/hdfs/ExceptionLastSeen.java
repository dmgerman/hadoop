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
name|classification
operator|.
name|InterfaceAudience
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
name|nio
operator|.
name|channels
operator|.
name|ClosedChannelException
import|;
end_import

begin_comment
comment|/**  * The exception last seen by the {@link DataStreamer} or  * {@link DFSOutputStream}.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|ExceptionLastSeen
class|class
name|ExceptionLastSeen
block|{
DECL|field|thrown
specifier|private
name|IOException
name|thrown
decl_stmt|;
comment|/** Get the last seen exception. */
DECL|method|get ()
specifier|synchronized
specifier|protected
name|IOException
name|get
parameter_list|()
block|{
return|return
name|thrown
return|;
block|}
comment|/**    * Set the last seen exception.    * @param t the exception.    */
DECL|method|set (Throwable t)
specifier|synchronized
name|void
name|set
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
assert|assert
name|t
operator|!=
literal|null
assert|;
name|this
operator|.
name|thrown
operator|=
name|t
operator|instanceof
name|IOException
condition|?
operator|(
name|IOException
operator|)
name|t
else|:
operator|new
name|IOException
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
comment|/** Clear the last seen exception. */
DECL|method|clear ()
specifier|synchronized
name|void
name|clear
parameter_list|()
block|{
name|thrown
operator|=
literal|null
expr_stmt|;
block|}
comment|/**    * Check if there already is an exception. Throw the exception if exist.    *    * @param resetToNull set to true to reset exception to null after calling    *                    this function.    * @throws IOException on existing IOException.    */
DECL|method|check (boolean resetToNull)
specifier|synchronized
name|void
name|check
parameter_list|(
name|boolean
name|resetToNull
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|thrown
operator|!=
literal|null
condition|)
block|{
specifier|final
name|IOException
name|e
init|=
name|thrown
decl_stmt|;
if|if
condition|(
name|resetToNull
condition|)
block|{
name|thrown
operator|=
literal|null
expr_stmt|;
block|}
throw|throw
name|e
throw|;
block|}
block|}
DECL|method|throwException4Close ()
specifier|synchronized
name|void
name|throwException4Close
parameter_list|()
throws|throws
name|IOException
block|{
name|check
argument_list|(
literal|false
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ClosedChannelException
argument_list|()
throw|;
block|}
block|}
end_class

end_unit

