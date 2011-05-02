begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ipc
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ipc
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
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_comment
comment|/**  * a class wraps around a server's proxy,   * containing a list of its supported methods.  *   * A list of methods with a value of null indicates that the client and server  * have the same protocol.  */
end_comment

begin_class
DECL|class|ProtocolProxy
specifier|public
class|class
name|ProtocolProxy
parameter_list|<
name|T
parameter_list|>
block|{
DECL|field|protocol
specifier|private
name|Class
argument_list|<
name|T
argument_list|>
name|protocol
decl_stmt|;
DECL|field|proxy
specifier|private
name|T
name|proxy
decl_stmt|;
DECL|field|serverMethods
specifier|private
name|HashSet
argument_list|<
name|Integer
argument_list|>
name|serverMethods
init|=
literal|null
decl_stmt|;
comment|/**    * Constructor    *     * @param protocol protocol class    * @param proxy its proxy    * @param serverMethods a list of hash codes of the methods that it supports    * @throws ClassNotFoundException     */
DECL|method|ProtocolProxy (Class<T> protocol, T proxy, int[] serverMethods)
specifier|public
name|ProtocolProxy
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|protocol
parameter_list|,
name|T
name|proxy
parameter_list|,
name|int
index|[]
name|serverMethods
parameter_list|)
block|{
name|this
operator|.
name|protocol
operator|=
name|protocol
expr_stmt|;
name|this
operator|.
name|proxy
operator|=
name|proxy
expr_stmt|;
if|if
condition|(
name|serverMethods
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|serverMethods
operator|=
operator|new
name|HashSet
argument_list|<
name|Integer
argument_list|>
argument_list|(
name|serverMethods
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|method
range|:
name|serverMethods
control|)
block|{
name|this
operator|.
name|serverMethods
operator|.
name|add
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
name|method
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/*    * Get the proxy    */
DECL|method|getProxy ()
specifier|public
name|T
name|getProxy
parameter_list|()
block|{
return|return
name|proxy
return|;
block|}
comment|/**    * Check if a method is supported by the server or not    *     * @param methodName a method's name in String format    * @param parameterTypes a method's parameter types    * @return true if the method is supported by the server    */
DECL|method|isMethodSupported (String methodName, Class<?>... parameterTypes)
specifier|public
name|boolean
name|isMethodSupported
parameter_list|(
name|String
name|methodName
parameter_list|,
name|Class
argument_list|<
name|?
argument_list|>
modifier|...
name|parameterTypes
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|serverMethods
operator|==
literal|null
condition|)
block|{
comment|// client& server have the same protocol
return|return
literal|true
return|;
block|}
name|Method
name|method
decl_stmt|;
try|try
block|{
name|method
operator|=
name|protocol
operator|.
name|getDeclaredMethod
argument_list|(
name|methodName
argument_list|,
name|parameterTypes
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SecurityException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
return|return
name|serverMethods
operator|.
name|contains
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
name|ProtocolSignature
operator|.
name|getFingerprint
argument_list|(
name|method
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

