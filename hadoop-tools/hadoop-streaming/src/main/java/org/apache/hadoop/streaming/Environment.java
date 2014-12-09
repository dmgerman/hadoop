begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.streaming
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|streaming
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|Charset
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
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
name|io
operator|.
name|IOUtils
import|;
end_import

begin_comment
comment|/**  * This is a class used to get the current environment  * on the host machines running the map/reduce. This class  * assumes that setting the environment in streaming is   * allowed on windows/ix/linuz/freebsd/sunos/solaris/hp-ux  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|Environment
specifier|public
class|class
name|Environment
extends|extends
name|Properties
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
DECL|method|Environment ()
specifier|public
name|Environment
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Extend this code to fit all operating
comment|// environments that you expect to run in
comment|// http://lopica.sourceforge.net/os.html
name|String
name|command
init|=
literal|null
decl_stmt|;
name|String
name|OS
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"os.name"
argument_list|)
decl_stmt|;
name|String
name|lowerOs
init|=
name|OS
operator|.
name|toLowerCase
argument_list|()
decl_stmt|;
if|if
condition|(
name|OS
operator|.
name|indexOf
argument_list|(
literal|"Windows"
argument_list|)
operator|>
operator|-
literal|1
condition|)
block|{
name|command
operator|=
literal|"cmd /C set"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|lowerOs
operator|.
name|indexOf
argument_list|(
literal|"ix"
argument_list|)
operator|>
operator|-
literal|1
operator|||
name|lowerOs
operator|.
name|indexOf
argument_list|(
literal|"linux"
argument_list|)
operator|>
operator|-
literal|1
operator|||
name|lowerOs
operator|.
name|indexOf
argument_list|(
literal|"freebsd"
argument_list|)
operator|>
operator|-
literal|1
operator|||
name|lowerOs
operator|.
name|indexOf
argument_list|(
literal|"sunos"
argument_list|)
operator|>
operator|-
literal|1
operator|||
name|lowerOs
operator|.
name|indexOf
argument_list|(
literal|"solaris"
argument_list|)
operator|>
operator|-
literal|1
operator|||
name|lowerOs
operator|.
name|indexOf
argument_list|(
literal|"hp-ux"
argument_list|)
operator|>
operator|-
literal|1
condition|)
block|{
name|command
operator|=
literal|"env"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|lowerOs
operator|.
name|startsWith
argument_list|(
literal|"mac os x"
argument_list|)
operator|||
name|lowerOs
operator|.
name|startsWith
argument_list|(
literal|"darwin"
argument_list|)
condition|)
block|{
name|command
operator|=
literal|"env"
expr_stmt|;
block|}
else|else
block|{
comment|// Add others here
block|}
if|if
condition|(
name|command
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Operating system "
operator|+
name|OS
operator|+
literal|" not supported by this class"
argument_list|)
throw|;
block|}
comment|// Read the environment variables
name|Process
name|pid
init|=
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|exec
argument_list|(
name|command
argument_list|)
decl_stmt|;
name|BufferedReader
name|in
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|pid
operator|.
name|getInputStream
argument_list|()
argument_list|,
name|Charset
operator|.
name|forName
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|String
name|line
init|=
name|in
operator|.
name|readLine
argument_list|()
decl_stmt|;
if|if
condition|(
name|line
operator|==
literal|null
condition|)
break|break;
name|int
name|p
init|=
name|line
operator|.
name|indexOf
argument_list|(
literal|"="
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|!=
operator|-
literal|1
condition|)
block|{
name|String
name|name
init|=
name|line
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|p
argument_list|)
decl_stmt|;
name|String
name|value
init|=
name|line
operator|.
name|substring
argument_list|(
name|p
operator|+
literal|1
argument_list|)
decl_stmt|;
name|setProperty
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
name|in
operator|=
literal|null
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|pid
operator|.
name|waitFor
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|// to be used with Runtime.exec(String[] cmdarray, String[] envp)
DECL|method|toArray ()
name|String
index|[]
name|toArray
parameter_list|()
block|{
name|String
index|[]
name|arr
init|=
operator|new
name|String
index|[
name|super
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|Enumeration
argument_list|<
name|Object
argument_list|>
name|it
init|=
name|super
operator|.
name|keys
argument_list|()
decl_stmt|;
name|int
name|i
init|=
operator|-
literal|1
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|String
name|key
init|=
operator|(
name|String
operator|)
name|it
operator|.
name|nextElement
argument_list|()
decl_stmt|;
name|String
name|val
init|=
operator|(
name|String
operator|)
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|i
operator|++
expr_stmt|;
name|arr
index|[
name|i
index|]
operator|=
name|key
operator|+
literal|"="
operator|+
name|val
expr_stmt|;
block|}
return|return
name|arr
return|;
block|}
DECL|method|toMap ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|toMap
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|Enumeration
argument_list|<
name|Object
argument_list|>
name|it
init|=
name|super
operator|.
name|keys
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|String
name|key
init|=
operator|(
name|String
operator|)
name|it
operator|.
name|nextElement
argument_list|()
decl_stmt|;
name|String
name|val
init|=
operator|(
name|String
operator|)
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
return|return
name|map
return|;
block|}
DECL|method|getHost ()
specifier|public
name|String
name|getHost
parameter_list|()
block|{
name|String
name|host
init|=
name|getProperty
argument_list|(
literal|"HOST"
argument_list|)
decl_stmt|;
if|if
condition|(
name|host
operator|==
literal|null
condition|)
block|{
comment|// HOST isn't always in the environment
try|try
block|{
name|host
operator|=
name|InetAddress
operator|.
name|getLocalHost
argument_list|()
operator|.
name|getHostName
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|io
parameter_list|)
block|{
name|io
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|host
return|;
block|}
block|}
end_class

end_unit

