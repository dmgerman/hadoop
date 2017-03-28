begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.scm.exceptions
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|scm
operator|.
name|exceptions
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
comment|/**  * Exception thrown by SCM.  */
end_comment

begin_class
DECL|class|SCMException
specifier|public
class|class
name|SCMException
extends|extends
name|IOException
block|{
comment|/**    * Error codes to make it easy to decode these exceptions.    */
DECL|enum|ResultCodes
specifier|public
enum|enum
name|ResultCodes
block|{
DECL|enumConstant|FAILED_TO_LOAD_NODEPOOL
name|FAILED_TO_LOAD_NODEPOOL
block|,
DECL|enumConstant|NODE_NOT_FOUND_IN_NODEPOOL
name|NODE_NOT_FOUND_IN_NODEPOOL
block|,   }
DECL|field|result
specifier|private
specifier|final
name|ResultCodes
name|result
decl_stmt|;
comment|/**    * Constructs an {@code IOException} with {@code null}    * as its error detail message.    */
DECL|method|SCMException (ResultCodes result)
specifier|public
name|SCMException
parameter_list|(
name|ResultCodes
name|result
parameter_list|)
block|{
name|this
operator|.
name|result
operator|=
name|result
expr_stmt|;
block|}
comment|/**    * Constructs an {@code IOException} with the specified detail message.    *    * @param message The detail message (which is saved for later retrieval by    * the    * {@link #getMessage()} method)    */
DECL|method|SCMException (String message, ResultCodes result)
specifier|public
name|SCMException
parameter_list|(
name|String
name|message
parameter_list|,
name|ResultCodes
name|result
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|this
operator|.
name|result
operator|=
name|result
expr_stmt|;
block|}
comment|/**    * Constructs an {@code IOException} with the specified detail message    * and cause.    *<p>    *<p> Note that the detail message associated with {@code cause} is    *<i>not</i> automatically incorporated into this exception's detail    * message.    *    * @param message The detail message (which is saved for later retrieval by    * the    * {@link #getMessage()} method)    * @param cause The cause (which is saved for later retrieval by the {@link    * #getCause()} method).  (A null value is permitted, and indicates that the    * cause is nonexistent or unknown.)    * @since 1.6    */
DECL|method|SCMException (String message, Throwable cause, ResultCodes result)
specifier|public
name|SCMException
parameter_list|(
name|String
name|message
parameter_list|,
name|Throwable
name|cause
parameter_list|,
name|ResultCodes
name|result
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|,
name|cause
argument_list|)
expr_stmt|;
name|this
operator|.
name|result
operator|=
name|result
expr_stmt|;
block|}
comment|/**    * Constructs an {@code IOException} with the specified cause and a    * detail message of {@code (cause==null ? null : cause.toString())}    * (which typically contains the class and detail message of {@code cause}).    * This constructor is useful for IO exceptions that are little more    * than wrappers for other throwables.    *    * @param cause The cause (which is saved for later retrieval by the {@link    * #getCause()} method).  (A null value is permitted, and indicates that the    * cause is nonexistent or unknown.)    * @since 1.6    */
DECL|method|SCMException (Throwable cause, ResultCodes result)
specifier|public
name|SCMException
parameter_list|(
name|Throwable
name|cause
parameter_list|,
name|ResultCodes
name|result
parameter_list|)
block|{
name|super
argument_list|(
name|cause
argument_list|)
expr_stmt|;
name|this
operator|.
name|result
operator|=
name|result
expr_stmt|;
block|}
block|}
end_class

end_unit

