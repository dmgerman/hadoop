begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.security.authorize
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|authorize
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
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
name|security
operator|.
name|AccessControlException
import|;
end_import

begin_comment
comment|/**  * An exception class for authorization-related issues.  *   * This class<em>does not</em> provide the stack trace for security purposes.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|AuthorizationException
specifier|public
class|class
name|AuthorizationException
extends|extends
name|AccessControlException
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
DECL|method|AuthorizationException ()
specifier|public
name|AuthorizationException
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
DECL|method|AuthorizationException (String message)
specifier|public
name|AuthorizationException
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructs a new exception with the specified cause and a detail    * message of<tt>(cause==null ? null : cause.toString())</tt> (which    * typically contains the class and detail message of<tt>cause</tt>).    * @param  cause the cause (which is saved for later retrieval by the    *         {@link #getCause()} method).  (A<tt>null</tt> value is    *         permitted, and indicates that the cause is nonexistent or    *         unknown.)    */
DECL|method|AuthorizationException (Throwable cause)
specifier|public
name|AuthorizationException
parameter_list|(
name|Throwable
name|cause
parameter_list|)
block|{
name|super
argument_list|(
name|cause
argument_list|)
expr_stmt|;
block|}
DECL|field|stackTrace
specifier|private
specifier|static
name|StackTraceElement
index|[]
name|stackTrace
init|=
operator|new
name|StackTraceElement
index|[
literal|0
index|]
decl_stmt|;
annotation|@
name|Override
DECL|method|getStackTrace ()
specifier|public
name|StackTraceElement
index|[]
name|getStackTrace
parameter_list|()
block|{
comment|// Do not provide the stack-trace
return|return
name|stackTrace
return|;
block|}
annotation|@
name|Override
DECL|method|printStackTrace ()
specifier|public
name|void
name|printStackTrace
parameter_list|()
block|{
name|printStackTrace
argument_list|(
name|System
operator|.
name|err
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|printStackTrace (PrintStream s)
specifier|public
name|void
name|printStackTrace
parameter_list|(
name|PrintStream
name|s
parameter_list|)
block|{
comment|// Do not provide the stack-trace
name|s
operator|.
name|println
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|printStackTrace (PrintWriter s)
specifier|public
name|void
name|printStackTrace
parameter_list|(
name|PrintWriter
name|s
parameter_list|)
block|{
comment|// Do not provide the stack-trace
name|s
operator|.
name|println
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

