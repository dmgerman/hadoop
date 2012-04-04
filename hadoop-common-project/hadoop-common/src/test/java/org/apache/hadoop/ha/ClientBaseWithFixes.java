begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ha
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ha
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|ObjectName
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|test
operator|.
name|ClientBase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|test
operator|.
name|JMXEnv
import|;
end_import

begin_comment
comment|/**  * A subclass of ZK's ClientBase testing utility, with some fixes  * necessary for running in the Hadoop context.  */
end_comment

begin_class
DECL|class|ClientBaseWithFixes
specifier|public
class|class
name|ClientBaseWithFixes
extends|extends
name|ClientBase
block|{
comment|/**    * When running on the Jenkins setup, we need to ensure that this    * build directory exists before running the tests.    */
annotation|@
name|Override
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
comment|// build.test.dir is used by zookeeper
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"build.test.dir"
argument_list|,
literal|"build"
argument_list|)
argument_list|)
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
block|}
comment|/**    * ZK seems to have a bug when we muck with its sessions    * behind its back, causing disconnects, etc. This bug    * ends up leaving JMX beans around at the end of the test,    * and ClientBase's teardown method will throw an exception    * if it finds JMX beans leaked. So, clear them out there    * to workaround the ZK bug. See ZOOKEEPER-1438.    */
annotation|@
name|Override
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|Set
argument_list|<
name|ObjectName
argument_list|>
name|names
init|=
name|JMXEnv
operator|.
name|ensureAll
argument_list|()
decl_stmt|;
for|for
control|(
name|ObjectName
name|n
range|:
name|names
control|)
block|{
try|try
block|{
name|JMXEnv
operator|.
name|conn
argument_list|()
operator|.
name|unregisterMBean
argument_list|(
name|n
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
comment|// ignore
block|}
block|}
block|}
block|}
end_class

end_unit

