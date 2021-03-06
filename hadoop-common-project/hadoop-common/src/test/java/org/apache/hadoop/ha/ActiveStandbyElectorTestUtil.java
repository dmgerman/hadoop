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
name|util
operator|.
name|Arrays
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
name|test
operator|.
name|MultithreadedTestUtil
operator|.
name|TestContext
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
name|util
operator|.
name|StringUtils
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
name|util
operator|.
name|Time
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
name|KeeperException
operator|.
name|NoNodeException
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
name|data
operator|.
name|Stat
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
name|server
operator|.
name|ZooKeeperServer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_class
DECL|class|ActiveStandbyElectorTestUtil
specifier|public
specifier|abstract
class|class
name|ActiveStandbyElectorTestUtil
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ActiveStandbyElectorTestUtil
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|LOG_INTERVAL_MS
specifier|private
specifier|static
specifier|final
name|long
name|LOG_INTERVAL_MS
init|=
literal|500
decl_stmt|;
DECL|method|waitForActiveLockData (TestContext ctx, ZooKeeperServer zks, String parentDir, byte[] activeData)
specifier|public
specifier|static
name|void
name|waitForActiveLockData
parameter_list|(
name|TestContext
name|ctx
parameter_list|,
name|ZooKeeperServer
name|zks
parameter_list|,
name|String
name|parentDir
parameter_list|,
name|byte
index|[]
name|activeData
parameter_list|)
throws|throws
name|Exception
block|{
name|long
name|st
init|=
name|Time
operator|.
name|now
argument_list|()
decl_stmt|;
name|long
name|lastPrint
init|=
name|st
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|ctx
operator|!=
literal|null
condition|)
block|{
name|ctx
operator|.
name|checkException
argument_list|()
expr_stmt|;
block|}
try|try
block|{
name|Stat
name|stat
init|=
operator|new
name|Stat
argument_list|()
decl_stmt|;
name|byte
index|[]
name|data
init|=
name|zks
operator|.
name|getZKDatabase
argument_list|()
operator|.
name|getData
argument_list|(
name|parentDir
operator|+
literal|"/"
operator|+
name|ActiveStandbyElector
operator|.
name|LOCK_FILENAME
argument_list|,
name|stat
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|activeData
operator|!=
literal|null
operator|&&
name|Arrays
operator|.
name|equals
argument_list|(
name|activeData
argument_list|,
name|data
argument_list|)
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|Time
operator|.
name|now
argument_list|()
operator|>
name|lastPrint
operator|+
name|LOG_INTERVAL_MS
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Cur data: "
operator|+
name|StringUtils
operator|.
name|byteToHexString
argument_list|(
name|data
argument_list|)
argument_list|)
expr_stmt|;
name|lastPrint
operator|=
name|Time
operator|.
name|now
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|NoNodeException
name|nne
parameter_list|)
block|{
if|if
condition|(
name|activeData
operator|==
literal|null
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|Time
operator|.
name|now
argument_list|()
operator|>
name|lastPrint
operator|+
name|LOG_INTERVAL_MS
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Cur data: no node"
argument_list|)
expr_stmt|;
name|lastPrint
operator|=
name|Time
operator|.
name|now
argument_list|()
expr_stmt|;
block|}
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|50
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|waitForElectorState (TestContext ctx, ActiveStandbyElector elector, ActiveStandbyElector.State state)
specifier|public
specifier|static
name|void
name|waitForElectorState
parameter_list|(
name|TestContext
name|ctx
parameter_list|,
name|ActiveStandbyElector
name|elector
parameter_list|,
name|ActiveStandbyElector
operator|.
name|State
name|state
parameter_list|)
throws|throws
name|Exception
block|{
while|while
condition|(
name|elector
operator|.
name|getStateForTests
argument_list|()
operator|!=
name|state
condition|)
block|{
if|if
condition|(
name|ctx
operator|!=
literal|null
condition|)
block|{
name|ctx
operator|.
name|checkException
argument_list|()
expr_stmt|;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|50
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

