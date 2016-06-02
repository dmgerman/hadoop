begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.net
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|net
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
name|net
operator|.
name|ServerSocket
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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

begin_class
DECL|class|ServerSocketUtil
specifier|public
class|class
name|ServerSocketUtil
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|ServerSocketUtil
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|rand
specifier|private
specifier|static
name|Random
name|rand
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
comment|/**    * Port scan& allocate is how most other apps find ports    *     * @param port given port    * @param retries number of retries    * @return    * @throws IOException    */
DECL|method|getPort (int port, int retries)
specifier|public
specifier|static
name|int
name|getPort
parameter_list|(
name|int
name|port
parameter_list|,
name|int
name|retries
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|tryPort
init|=
name|port
decl_stmt|;
name|int
name|tries
init|=
literal|0
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|tries
operator|>
literal|0
operator|||
name|tryPort
operator|==
literal|0
condition|)
block|{
name|tryPort
operator|=
name|port
operator|+
name|rand
operator|.
name|nextInt
argument_list|(
literal|65535
operator|-
name|port
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|tryPort
operator|==
literal|0
condition|)
block|{
continue|continue;
block|}
try|try
init|(
name|ServerSocket
name|s
init|=
operator|new
name|ServerSocket
argument_list|(
name|tryPort
argument_list|)
init|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Using port "
operator|+
name|tryPort
argument_list|)
expr_stmt|;
return|return
name|tryPort
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|tries
operator|++
expr_stmt|;
if|if
condition|(
name|tries
operator|>=
name|retries
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Port is already in use; giving up"
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Port is already in use; trying again"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/**    * Check whether port is available or not.    *    * @param port given port    * @return    */
DECL|method|isPortAvailable (int port)
specifier|private
specifier|static
name|boolean
name|isPortAvailable
parameter_list|(
name|int
name|port
parameter_list|)
block|{
try|try
init|(
name|ServerSocket
name|s
init|=
operator|new
name|ServerSocket
argument_list|(
name|port
argument_list|)
init|)
block|{
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
comment|/**    * Wait till the port available.    *    * @param port given port    * @param retries number of retries for given port    * @return    * @throws InterruptedException    * @throws IOException    */
DECL|method|waitForPort (int port, int retries)
specifier|public
specifier|static
name|int
name|waitForPort
parameter_list|(
name|int
name|port
parameter_list|,
name|int
name|retries
parameter_list|)
throws|throws
name|InterruptedException
throws|,
name|IOException
block|{
name|int
name|tries
init|=
literal|0
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|isPortAvailable
argument_list|(
name|port
argument_list|)
condition|)
block|{
return|return
name|port
return|;
block|}
else|else
block|{
name|tries
operator|++
expr_stmt|;
if|if
condition|(
name|tries
operator|>=
name|retries
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Port is already in use; giving up after "
operator|+
name|tries
operator|+
literal|" times."
argument_list|)
throw|;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

