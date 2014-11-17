begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode.web.webhdfs
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
name|datanode
operator|.
name|web
operator|.
name|webhdfs
package|;
end_package

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

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|buffer
operator|.
name|Unpooled
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|handler
operator|.
name|codec
operator|.
name|http
operator|.
name|DefaultFullHttpResponse
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|handler
operator|.
name|codec
operator|.
name|http
operator|.
name|HttpResponseStatus
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
name|ipc
operator|.
name|StandbyException
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|token
operator|.
name|SecretManager
import|;
end_import

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
import|import static
name|io
operator|.
name|netty
operator|.
name|handler
operator|.
name|codec
operator|.
name|http
operator|.
name|HttpHeaders
operator|.
name|Names
operator|.
name|CONTENT_LENGTH
import|;
end_import

begin_import
import|import static
name|io
operator|.
name|netty
operator|.
name|handler
operator|.
name|codec
operator|.
name|http
operator|.
name|HttpHeaders
operator|.
name|Names
operator|.
name|CONTENT_TYPE
import|;
end_import

begin_import
import|import static
name|io
operator|.
name|netty
operator|.
name|handler
operator|.
name|codec
operator|.
name|http
operator|.
name|HttpResponseStatus
operator|.
name|BAD_REQUEST
import|;
end_import

begin_import
import|import static
name|io
operator|.
name|netty
operator|.
name|handler
operator|.
name|codec
operator|.
name|http
operator|.
name|HttpResponseStatus
operator|.
name|FORBIDDEN
import|;
end_import

begin_import
import|import static
name|io
operator|.
name|netty
operator|.
name|handler
operator|.
name|codec
operator|.
name|http
operator|.
name|HttpResponseStatus
operator|.
name|INTERNAL_SERVER_ERROR
import|;
end_import

begin_import
import|import static
name|io
operator|.
name|netty
operator|.
name|handler
operator|.
name|codec
operator|.
name|http
operator|.
name|HttpResponseStatus
operator|.
name|NOT_FOUND
import|;
end_import

begin_import
import|import static
name|io
operator|.
name|netty
operator|.
name|handler
operator|.
name|codec
operator|.
name|http
operator|.
name|HttpVersion
operator|.
name|HTTP_1_1
import|;
end_import

begin_import
import|import static
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
name|datanode
operator|.
name|web
operator|.
name|webhdfs
operator|.
name|WebHdfsHandler
operator|.
name|APPLICATION_JSON
import|;
end_import

begin_class
DECL|class|ExceptionHandler
class|class
name|ExceptionHandler
block|{
DECL|field|LOG
specifier|static
name|Log
name|LOG
init|=
name|WebHdfsHandler
operator|.
name|LOG
decl_stmt|;
DECL|method|exceptionCaught (Throwable cause)
specifier|static
name|DefaultFullHttpResponse
name|exceptionCaught
parameter_list|(
name|Throwable
name|cause
parameter_list|)
block|{
name|Exception
name|e
init|=
name|cause
operator|instanceof
name|Exception
condition|?
operator|(
name|Exception
operator|)
name|cause
else|:
operator|new
name|Exception
argument_list|(
name|cause
argument_list|)
decl_stmt|;
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
elseif|else
if|if
condition|(
name|e
operator|instanceof
name|ContainerException
operator|||
name|e
operator|instanceof
name|SecurityException
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
elseif|else
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
name|HttpResponseStatus
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
name|INTERNAL_SERVER_ERROR
expr_stmt|;
block|}
specifier|final
name|byte
index|[]
name|js
init|=
name|JsonUtil
operator|.
name|toJsonString
argument_list|(
name|e
argument_list|)
operator|.
name|getBytes
argument_list|()
decl_stmt|;
name|DefaultFullHttpResponse
name|resp
init|=
operator|new
name|DefaultFullHttpResponse
argument_list|(
name|HTTP_1_1
argument_list|,
name|s
argument_list|,
name|Unpooled
operator|.
name|wrappedBuffer
argument_list|(
name|js
argument_list|)
argument_list|)
decl_stmt|;
name|resp
operator|.
name|headers
argument_list|()
operator|.
name|set
argument_list|(
name|CONTENT_TYPE
argument_list|,
name|APPLICATION_JSON
argument_list|)
expr_stmt|;
name|resp
operator|.
name|headers
argument_list|()
operator|.
name|set
argument_list|(
name|CONTENT_LENGTH
argument_list|,
name|js
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
name|resp
return|;
block|}
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
name|e
operator|instanceof
name|SecurityException
condition|)
block|{
comment|// For the issue reported in HDFS-6475, if SecurityException's cause
comment|// is InvalidToken, and the InvalidToken's cause is StandbyException,
comment|// return StandbyException; Otherwise, leave the exception as is,
comment|// since they are handled elsewhere. See HDFS-6588.
if|if
condition|(
name|t
operator|!=
literal|null
operator|&&
name|t
operator|instanceof
name|SecretManager
operator|.
name|InvalidToken
condition|)
block|{
specifier|final
name|Throwable
name|t1
init|=
name|t
operator|.
name|getCause
argument_list|()
decl_stmt|;
if|if
condition|(
name|t1
operator|!=
literal|null
operator|&&
name|t1
operator|instanceof
name|StandbyException
condition|)
block|{
name|e
operator|=
operator|(
name|StandbyException
operator|)
name|t1
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
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
name|t
expr_stmt|;
block|}
block|}
return|return
name|e
return|;
block|}
block|}
end_class

end_unit

