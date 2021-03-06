begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|Configuration
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|Thread
operator|.
name|sleep
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
name|fs
operator|.
name|CommonConfigurationKeysPublic
operator|.
name|SERVICE_SHUTDOWN_TIMEOUT
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
name|fs
operator|.
name|CommonConfigurationKeysPublic
operator|.
name|SERVICE_SHUTDOWN_TIMEOUT_DEFAULT
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotNull
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_class
DECL|class|TestShutdownHookManager
specifier|public
class|class
name|TestShutdownHookManager
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestShutdownHookManager
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
comment|/**    * A new instance of ShutdownHookManager to ensure parallel tests    * don't have shared context.    */
DECL|field|mgr
specifier|private
specifier|final
name|ShutdownHookManager
name|mgr
init|=
operator|new
name|ShutdownHookManager
argument_list|()
decl_stmt|;
comment|/**    * Verify hook registration, then execute the hook callback stage    * of shutdown to verify invocation, execution order and timeout    * processing.    */
annotation|@
name|Test
DECL|method|shutdownHookManager ()
specifier|public
name|void
name|shutdownHookManager
parameter_list|()
block|{
name|assertNotNull
argument_list|(
literal|"No ShutdownHookManager"
argument_list|,
name|mgr
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|mgr
operator|.
name|getShutdownHooksInOrder
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Hook
name|hook1
init|=
operator|new
name|Hook
argument_list|(
literal|"hook1"
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|Hook
name|hook2
init|=
operator|new
name|Hook
argument_list|(
literal|"hook2"
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|Hook
name|hook3
init|=
operator|new
name|Hook
argument_list|(
literal|"hook3"
argument_list|,
literal|1000
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|Hook
name|hook4
init|=
operator|new
name|Hook
argument_list|(
literal|"hook4"
argument_list|,
literal|25000
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Hook
name|hook5
init|=
operator|new
name|Hook
argument_list|(
literal|"hook5"
argument_list|,
operator|(
name|SERVICE_SHUTDOWN_TIMEOUT_DEFAULT
operator|+
literal|1
operator|)
operator|*
literal|1000
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|mgr
operator|.
name|addShutdownHook
argument_list|(
name|hook1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|mgr
operator|.
name|hasShutdownHook
argument_list|(
name|hook1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|mgr
operator|.
name|getShutdownHooksInOrder
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|hook1
argument_list|,
name|mgr
operator|.
name|getShutdownHooksInOrder
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getHook
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|mgr
operator|.
name|removeShutdownHook
argument_list|(
name|hook1
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|mgr
operator|.
name|hasShutdownHook
argument_list|(
name|hook1
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|mgr
operator|.
name|removeShutdownHook
argument_list|(
name|hook1
argument_list|)
argument_list|)
expr_stmt|;
name|mgr
operator|.
name|addShutdownHook
argument_list|(
name|hook1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|mgr
operator|.
name|hasShutdownHook
argument_list|(
name|hook1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|mgr
operator|.
name|getShutdownHooksInOrder
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|SERVICE_SHUTDOWN_TIMEOUT_DEFAULT
argument_list|,
name|mgr
operator|.
name|getShutdownHooksInOrder
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getTimeout
argument_list|()
argument_list|)
expr_stmt|;
name|mgr
operator|.
name|addShutdownHook
argument_list|(
name|hook2
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|mgr
operator|.
name|hasShutdownHook
argument_list|(
name|hook1
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|mgr
operator|.
name|hasShutdownHook
argument_list|(
name|hook2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|mgr
operator|.
name|getShutdownHooksInOrder
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|hook2
argument_list|,
name|mgr
operator|.
name|getShutdownHooksInOrder
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getHook
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|hook1
argument_list|,
name|mgr
operator|.
name|getShutdownHooksInOrder
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getHook
argument_list|()
argument_list|)
expr_stmt|;
comment|// Test hook finish without timeout
name|mgr
operator|.
name|addShutdownHook
argument_list|(
name|hook3
argument_list|,
literal|2
argument_list|,
literal|4
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|mgr
operator|.
name|hasShutdownHook
argument_list|(
name|hook3
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|hook3
argument_list|,
name|mgr
operator|.
name|getShutdownHooksInOrder
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getHook
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|mgr
operator|.
name|getShutdownHooksInOrder
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getTimeout
argument_list|()
argument_list|)
expr_stmt|;
comment|// Test hook finish with timeout; highest priority
name|int
name|hook4timeout
init|=
literal|2
decl_stmt|;
name|mgr
operator|.
name|addShutdownHook
argument_list|(
name|hook4
argument_list|,
literal|3
argument_list|,
name|hook4timeout
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|mgr
operator|.
name|hasShutdownHook
argument_list|(
name|hook4
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|hook4
argument_list|,
name|mgr
operator|.
name|getShutdownHooksInOrder
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getHook
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|mgr
operator|.
name|getShutdownHooksInOrder
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getTimeout
argument_list|()
argument_list|)
expr_stmt|;
comment|// a default timeout hook and verify it gets the default timeout
name|mgr
operator|.
name|addShutdownHook
argument_list|(
name|hook5
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|ShutdownHookManager
operator|.
name|HookEntry
name|hookEntry5
init|=
name|mgr
operator|.
name|getShutdownHooksInOrder
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|hook5
argument_list|,
name|hookEntry5
operator|.
name|getHook
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"default timeout not used"
argument_list|,
name|ShutdownHookManager
operator|.
name|getShutdownTimeout
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
argument_list|,
name|hookEntry5
operator|.
name|getTimeout
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"hook priority"
argument_list|,
literal|5
argument_list|,
name|hookEntry5
operator|.
name|getPriority
argument_list|()
argument_list|)
expr_stmt|;
comment|// remove this to avoid a longer sleep in the test run
name|assertTrue
argument_list|(
literal|"failed to remove "
operator|+
name|hook5
argument_list|,
name|mgr
operator|.
name|removeShutdownHook
argument_list|(
name|hook5
argument_list|)
argument_list|)
expr_stmt|;
comment|// now execute the hook shutdown sequence
name|INVOCATION_COUNT
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"invoking executeShutdown()"
argument_list|)
expr_stmt|;
name|int
name|timeouts
init|=
name|mgr
operator|.
name|executeShutdown
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Shutdown completed"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Number of timed out hooks"
argument_list|,
literal|1
argument_list|,
name|timeouts
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|ShutdownHookManager
operator|.
name|HookEntry
argument_list|>
name|hooks
init|=
name|mgr
operator|.
name|getShutdownHooksInOrder
argument_list|()
decl_stmt|;
comment|// analyze the hooks
for|for
control|(
name|ShutdownHookManager
operator|.
name|HookEntry
name|entry
range|:
name|hooks
control|)
block|{
name|Hook
name|hook
init|=
operator|(
name|Hook
operator|)
name|entry
operator|.
name|getHook
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Was not invoked "
operator|+
name|hook
argument_list|,
name|hook
operator|.
name|invoked
argument_list|)
expr_stmt|;
comment|// did any hook raise an exception?
name|hook
operator|.
name|maybeThrowAssertion
argument_list|()
expr_stmt|;
block|}
comment|// check the state of some of the invoked hooks
comment|// hook4 was invoked first, but it timed out.
name|assertEquals
argument_list|(
literal|"Expected to be invoked first "
operator|+
name|hook4
argument_list|,
literal|1
argument_list|,
name|hook4
operator|.
name|invokedOrder
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Expected to time out "
operator|+
name|hook4
argument_list|,
name|hook4
operator|.
name|completed
argument_list|)
expr_stmt|;
comment|// hook1 completed, but in order after the others, so its start time
comment|// is the longest.
name|assertTrue
argument_list|(
literal|"Expected to complete "
operator|+
name|hook1
argument_list|,
name|hook1
operator|.
name|completed
argument_list|)
expr_stmt|;
name|long
name|invocationInterval
init|=
name|hook1
operator|.
name|startTime
operator|-
name|hook4
operator|.
name|startTime
decl_stmt|;
name|assertTrue
argument_list|(
literal|"invocation difference too short "
operator|+
name|invocationInterval
argument_list|,
name|invocationInterval
operator|>=
name|hook4timeout
operator|*
literal|1000
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"sleeping hook4 blocked other threads for "
operator|+
name|invocationInterval
argument_list|,
name|invocationInterval
operator|<
name|hook4
operator|.
name|sleepTime
argument_list|)
expr_stmt|;
comment|// finally, clear the hooks
name|mgr
operator|.
name|clearShutdownHooks
argument_list|()
expr_stmt|;
comment|// and verify that the hooks are empty
name|assertFalse
argument_list|(
name|mgr
operator|.
name|hasShutdownHook
argument_list|(
name|hook1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"shutdown hook list is not empty"
argument_list|,
literal|0
argument_list|,
name|mgr
operator|.
name|getShutdownHooksInOrder
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testShutdownTimeoutConfiguration ()
specifier|public
name|void
name|testShutdownTimeoutConfiguration
parameter_list|()
throws|throws
name|Throwable
block|{
comment|// set the shutdown timeout and verify it can be read back.
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|long
name|shutdownTimeout
init|=
literal|5
decl_stmt|;
name|conf
operator|.
name|setTimeDuration
argument_list|(
name|SERVICE_SHUTDOWN_TIMEOUT
argument_list|,
name|shutdownTimeout
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|SERVICE_SHUTDOWN_TIMEOUT
argument_list|,
name|shutdownTimeout
argument_list|,
name|ShutdownHookManager
operator|.
name|getShutdownTimeout
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Verify that low timeouts simply fall back to    * {@link ShutdownHookManager#TIMEOUT_MINIMUM}.    */
annotation|@
name|Test
DECL|method|testShutdownTimeoutBadConfiguration ()
specifier|public
name|void
name|testShutdownTimeoutBadConfiguration
parameter_list|()
throws|throws
name|Throwable
block|{
comment|// set the shutdown timeout and verify it can be read back.
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|long
name|shutdownTimeout
init|=
literal|50
decl_stmt|;
name|conf
operator|.
name|setTimeDuration
argument_list|(
name|SERVICE_SHUTDOWN_TIMEOUT
argument_list|,
name|shutdownTimeout
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|SERVICE_SHUTDOWN_TIMEOUT
argument_list|,
name|ShutdownHookManager
operator|.
name|TIMEOUT_MINIMUM
argument_list|,
name|ShutdownHookManager
operator|.
name|getShutdownTimeout
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Verifies that a hook cannot be re-registered: an attempt to do so    * will simply be ignored.    */
annotation|@
name|Test
DECL|method|testDuplicateRegistration ()
specifier|public
name|void
name|testDuplicateRegistration
parameter_list|()
throws|throws
name|Throwable
block|{
name|Hook
name|hook
init|=
operator|new
name|Hook
argument_list|(
literal|"hook1"
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|// add the hook
name|mgr
operator|.
name|addShutdownHook
argument_list|(
name|hook
argument_list|,
literal|2
argument_list|,
literal|1
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
comment|// add it at a higher priority. This will be ignored.
name|mgr
operator|.
name|addShutdownHook
argument_list|(
name|hook
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|ShutdownHookManager
operator|.
name|HookEntry
argument_list|>
name|hookList
init|=
name|mgr
operator|.
name|getShutdownHooksInOrder
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Hook added twice"
argument_list|,
literal|1
argument_list|,
name|hookList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|ShutdownHookManager
operator|.
name|HookEntry
name|entry
init|=
name|hookList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"priority of hook"
argument_list|,
literal|2
argument_list|,
name|entry
operator|.
name|getPriority
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"timeout of hook"
argument_list|,
literal|1
argument_list|,
name|entry
operator|.
name|getTimeout
argument_list|()
argument_list|)
expr_stmt|;
comment|// remove the hook
name|assertTrue
argument_list|(
literal|"failed to remove hook "
operator|+
name|hook
argument_list|,
name|mgr
operator|.
name|removeShutdownHook
argument_list|(
name|hook
argument_list|)
argument_list|)
expr_stmt|;
comment|// which will fail a second time
name|assertFalse
argument_list|(
literal|"expected hook removal to fail"
argument_list|,
name|mgr
operator|.
name|removeShutdownHook
argument_list|(
name|hook
argument_list|)
argument_list|)
expr_stmt|;
comment|// now register it
name|mgr
operator|.
name|addShutdownHook
argument_list|(
name|hook
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|hookList
operator|=
name|mgr
operator|.
name|getShutdownHooksInOrder
argument_list|()
expr_stmt|;
name|entry
operator|=
name|hookList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"priority of hook"
argument_list|,
literal|5
argument_list|,
name|entry
operator|.
name|getPriority
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
literal|"timeout of hook"
argument_list|,
literal|1
argument_list|,
name|entry
operator|.
name|getTimeout
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testShutdownRemove ()
specifier|public
name|void
name|testShutdownRemove
parameter_list|()
throws|throws
name|Throwable
block|{
name|assertNotNull
argument_list|(
literal|"No ShutdownHookManager"
argument_list|,
name|mgr
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|mgr
operator|.
name|getShutdownHooksInOrder
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Hook
name|hook1
init|=
operator|new
name|Hook
argument_list|(
literal|"hook1"
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|Hook
name|hook2
init|=
operator|new
name|Hook
argument_list|(
literal|"hook2"
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|mgr
operator|.
name|addShutdownHook
argument_list|(
name|hook1
argument_list|,
literal|9
argument_list|)
expr_stmt|;
comment|// create Hook1 with priority 9
name|assertTrue
argument_list|(
literal|"No hook1"
argument_list|,
name|mgr
operator|.
name|hasShutdownHook
argument_list|(
name|hook1
argument_list|)
argument_list|)
expr_stmt|;
comment|// hook1 lookup works
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|mgr
operator|.
name|getShutdownHooksInOrder
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// 1 hook
name|assertFalse
argument_list|(
literal|"Delete hook2 should not be allowed"
argument_list|,
name|mgr
operator|.
name|removeShutdownHook
argument_list|(
name|hook2
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Can't delete hook1"
argument_list|,
name|mgr
operator|.
name|removeShutdownHook
argument_list|(
name|hook1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|mgr
operator|.
name|getShutdownHooksInOrder
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|field|INVOCATION_COUNT
specifier|private
specifier|static
specifier|final
name|AtomicInteger
name|INVOCATION_COUNT
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
comment|/**    * Hooks for testing; save state for ease of asserting on    * invocation.    */
DECL|class|Hook
specifier|private
class|class
name|Hook
implements|implements
name|Runnable
block|{
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|sleepTime
specifier|private
specifier|final
name|long
name|sleepTime
decl_stmt|;
DECL|field|expectFailure
specifier|private
specifier|final
name|boolean
name|expectFailure
decl_stmt|;
DECL|field|assertion
specifier|private
name|AssertionError
name|assertion
decl_stmt|;
DECL|field|invoked
specifier|private
name|boolean
name|invoked
decl_stmt|;
DECL|field|invokedOrder
specifier|private
name|int
name|invokedOrder
decl_stmt|;
DECL|field|completed
specifier|private
name|boolean
name|completed
decl_stmt|;
DECL|field|interrupted
specifier|private
name|boolean
name|interrupted
decl_stmt|;
DECL|field|startTime
specifier|private
name|long
name|startTime
decl_stmt|;
DECL|method|Hook (final String name, final long sleepTime, final boolean expectFailure)
name|Hook
parameter_list|(
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|long
name|sleepTime
parameter_list|,
specifier|final
name|boolean
name|expectFailure
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|sleepTime
operator|=
name|sleepTime
expr_stmt|;
name|this
operator|.
name|expectFailure
operator|=
name|expectFailure
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|invoked
operator|=
literal|true
expr_stmt|;
name|invokedOrder
operator|=
name|INVOCATION_COUNT
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|startTime
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting shutdown of {} with sleep time of {}"
argument_list|,
name|name
argument_list|,
name|sleepTime
argument_list|)
expr_stmt|;
if|if
condition|(
name|sleepTime
operator|>
literal|0
condition|)
block|{
name|sleep
argument_list|(
name|sleepTime
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Completed shutdown of {}"
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|completed
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|expectFailure
condition|)
block|{
name|assertion
operator|=
operator|new
name|AssertionError
argument_list|(
literal|"Expected a failure of "
operator|+
name|name
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Shutdown {} interrupted exception"
argument_list|,
name|name
argument_list|,
name|ex
argument_list|)
expr_stmt|;
name|interrupted
operator|=
literal|true
expr_stmt|;
if|if
condition|(
operator|!
name|expectFailure
condition|)
block|{
name|assertion
operator|=
operator|new
name|AssertionError
argument_list|(
literal|"Timeout of "
operator|+
name|name
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
name|maybeThrowAssertion
argument_list|()
expr_stmt|;
block|}
comment|/**      * Raise any exception generated during the shutdown process.      * @throws AssertionError any assertion from the shutdown.      */
DECL|method|maybeThrowAssertion ()
name|void
name|maybeThrowAssertion
parameter_list|()
throws|throws
name|AssertionError
block|{
if|if
condition|(
name|assertion
operator|!=
literal|null
condition|)
block|{
throw|throw
name|assertion
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"Hook{"
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"name='"
argument_list|)
operator|.
name|append
argument_list|(
name|name
argument_list|)
operator|.
name|append
argument_list|(
literal|'\''
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", sleepTime="
argument_list|)
operator|.
name|append
argument_list|(
name|sleepTime
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", expectFailure="
argument_list|)
operator|.
name|append
argument_list|(
name|expectFailure
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", invoked="
argument_list|)
operator|.
name|append
argument_list|(
name|invoked
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", invokedOrder="
argument_list|)
operator|.
name|append
argument_list|(
name|invokedOrder
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", completed="
argument_list|)
operator|.
name|append
argument_list|(
name|completed
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", interrupted="
argument_list|)
operator|.
name|append
argument_list|(
name|interrupted
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'}'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

