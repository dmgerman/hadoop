begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.web.resources
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|web
operator|.
name|resources
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
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
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletResponse
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|Context
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|MediaType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|Response
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|ext
operator|.
name|ExceptionMapper
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|ext
operator|.
name|Provider
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
name|hdfs
operator|.
name|web
operator|.
name|JsonUtil
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
name|ipc
operator|.
name|RemoteException
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
name|authorize
operator|.
name|AuthorizationException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|api
operator|.
name|ParamException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|api
operator|.
name|container
operator|.
name|ContainerException
import|;
end_import

begin_comment
comment|/** Handle exceptions. */
end_comment

begin_class
annotation|@
name|Provider
DECL|class|ExceptionHandler
specifier|public
class|class
name|ExceptionHandler
implements|implements
name|ExceptionMapper
argument_list|<
name|Exception
argument_list|>
block|{
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
name|ExceptionHandler
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|toCause (Exception e)
specifier|private
specifier|static
name|Exception
name|toCause
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
specifier|final
name|Throwable
name|t
init|=
name|e
operator|.
name|getCause
argument_list|()
decl_stmt|;
if|if
condition|(
name|t
operator|!=
literal|null
operator|&&
name|t
operator|instanceof
name|Exception
condition|)
block|{
name|e
operator|=
operator|(
name|Exception
operator|)
name|e
operator|.
name|getCause
argument_list|()
expr_stmt|;
block|}
return|return
name|e
return|;
block|}
DECL|field|response
specifier|private
annotation|@
name|Context
name|HttpServletResponse
name|response
decl_stmt|;
annotation|@
name|Override
DECL|method|toResponse (Exception e)
specifier|public
name|Response
name|toResponse
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"GOT EXCEPITION"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
comment|//clear content type
name|response
operator|.
name|setContentType
argument_list|(
literal|null
argument_list|)
expr_stmt|;
comment|//Convert exception
if|if
condition|(
name|e
operator|instanceof
name|ParamException
condition|)
block|{
specifier|final
name|ParamException
name|paramexception
init|=
operator|(
name|ParamException
operator|)
name|e
decl_stmt|;
name|e
operator|=
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid value for webhdfs parameter \""
operator|+
name|paramexception
operator|.
name|getParameterName
argument_list|()
operator|+
literal|"\": "
operator|+
name|e
operator|.
name|getCause
argument_list|()
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|e
operator|instanceof
name|ContainerException
condition|)
block|{
name|e
operator|=
name|toCause
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|e
operator|instanceof
name|RemoteException
condition|)
block|{
name|e
operator|=
operator|(
operator|(
name|RemoteException
operator|)
name|e
operator|)
operator|.
name|unwrapRemoteException
argument_list|()
expr_stmt|;
block|}
comment|//Map response status
specifier|final
name|Response
operator|.
name|Status
name|s
decl_stmt|;
if|if
condition|(
name|e
operator|instanceof
name|SecurityException
condition|)
block|{
name|s
operator|=
name|Response
operator|.
name|Status
operator|.
name|FORBIDDEN
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|e
operator|instanceof
name|AuthorizationException
condition|)
block|{
name|s
operator|=
name|Response
operator|.
name|Status
operator|.
name|FORBIDDEN
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|e
operator|instanceof
name|FileNotFoundException
condition|)
block|{
name|s
operator|=
name|Response
operator|.
name|Status
operator|.
name|NOT_FOUND
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|e
operator|instanceof
name|IOException
condition|)
block|{
name|s
operator|=
name|Response
operator|.
name|Status
operator|.
name|FORBIDDEN
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|e
operator|instanceof
name|UnsupportedOperationException
condition|)
block|{
name|s
operator|=
name|Response
operator|.
name|Status
operator|.
name|BAD_REQUEST
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|e
operator|instanceof
name|IllegalArgumentException
condition|)
block|{
name|s
operator|=
name|Response
operator|.
name|Status
operator|.
name|BAD_REQUEST
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"INTERNAL_SERVER_ERROR"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|s
operator|=
name|Response
operator|.
name|Status
operator|.
name|INTERNAL_SERVER_ERROR
expr_stmt|;
block|}
specifier|final
name|String
name|js
init|=
name|JsonUtil
operator|.
name|toJsonString
argument_list|(
name|e
argument_list|)
decl_stmt|;
return|return
name|Response
operator|.
name|status
argument_list|(
name|s
argument_list|)
operator|.
name|type
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|)
operator|.
name|entity
argument_list|(
name|js
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
block|}
end_class

end_unit

