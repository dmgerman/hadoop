begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p/>  * http://www.apache.org/licenses/LICENSE-2.0  *<p/>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.diskbalancer
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
name|diskbalancer
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
comment|/**  * Disk Balancer Exceptions.  */
end_comment

begin_class
DECL|class|DiskBalancerException
specifier|public
class|class
name|DiskBalancerException
extends|extends
name|IOException
block|{
comment|/**    * Results returned by the RPC layer of DiskBalancer.    */
DECL|enum|Result
specifier|public
enum|enum
name|Result
block|{
DECL|enumConstant|DISK_BALANCER_NOT_ENABLED
name|DISK_BALANCER_NOT_ENABLED
block|,
DECL|enumConstant|INVALID_PLAN_VERSION
name|INVALID_PLAN_VERSION
block|,
DECL|enumConstant|INVALID_PLAN
name|INVALID_PLAN
block|,
DECL|enumConstant|INVALID_PLAN_HASH
name|INVALID_PLAN_HASH
block|,
DECL|enumConstant|OLD_PLAN_SUBMITTED
name|OLD_PLAN_SUBMITTED
block|,
DECL|enumConstant|DATANODE_ID_MISMATCH
name|DATANODE_ID_MISMATCH
block|,
DECL|enumConstant|MALFORMED_PLAN
name|MALFORMED_PLAN
block|,
DECL|enumConstant|PLAN_ALREADY_IN_PROGRESS
name|PLAN_ALREADY_IN_PROGRESS
block|,
DECL|enumConstant|INVALID_VOLUME
name|INVALID_VOLUME
block|,
DECL|enumConstant|INVALID_MOVE
name|INVALID_MOVE
block|,
DECL|enumConstant|INTERNAL_ERROR
name|INTERNAL_ERROR
block|,
DECL|enumConstant|NO_SUCH_PLAN
name|NO_SUCH_PLAN
block|,
DECL|enumConstant|UNKNOWN_KEY
name|UNKNOWN_KEY
block|,
DECL|enumConstant|INVALID_NODE
name|INVALID_NODE
block|,   }
DECL|field|result
specifier|private
specifier|final
name|Result
name|result
decl_stmt|;
comment|/**    * Constructs an {@code IOException} with the specified detail message.    *    * @param message The detail message (which is saved for later retrieval by    *                the {@link #getMessage()} method)    */
DECL|method|DiskBalancerException (String message, Result result)
specifier|public
name|DiskBalancerException
parameter_list|(
name|String
name|message
parameter_list|,
name|Result
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
comment|/**    * Constructs an {@code IOException} with the specified detail message and    * cause.    *<p/>    *<p> Note that the detail message associated with {@code cause} is    *<i>not</i>    * automatically incorporated into this exception's detail message.    *    * @param message The detail message (which is saved for later retrieval by    *                the    *                {@link #getMessage()} method)    * @param cause   The cause (which is saved for later retrieval by the {@link    *                #getCause()} method).  (A null value is permitted, and    *                indicates that the cause is nonexistent or unknown.)    */
DECL|method|DiskBalancerException (String message, Throwable cause, Result result)
specifier|public
name|DiskBalancerException
parameter_list|(
name|String
name|message
parameter_list|,
name|Throwable
name|cause
parameter_list|,
name|Result
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
comment|/**    * Constructs an {@code IOException} with the specified cause and a detail    * message of {@code (cause==null ? null : cause.toString())} (which typically    * contains the class and detail message of {@code cause}). This    * constructor is useful for IO exceptions that are little more than    * wrappers for other throwables.    *    * @param cause The cause (which is saved for later retrieval by the {@link    *              #getCause()} method).  (A null value is permitted, and    *              indicates    *              that the cause is nonexistent or unknown.)    */
DECL|method|DiskBalancerException (Throwable cause, Result result)
specifier|public
name|DiskBalancerException
parameter_list|(
name|Throwable
name|cause
parameter_list|,
name|Result
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
comment|/**    * Returns the result.    * @return int    */
DECL|method|getResult ()
specifier|public
name|Result
name|getResult
parameter_list|()
block|{
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

