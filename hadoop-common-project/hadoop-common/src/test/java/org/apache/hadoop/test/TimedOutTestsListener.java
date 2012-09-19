begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.test
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|test
package|;
end_package

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
name|java
operator|.
name|io
operator|.
name|StringWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|management
operator|.
name|LockInfo
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|management
operator|.
name|ManagementFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|management
operator|.
name|MonitorInfo
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|management
operator|.
name|ThreadInfo
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|management
operator|.
name|ThreadMXBean
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|DateFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|SimpleDateFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|notification
operator|.
name|Failure
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|notification
operator|.
name|RunListener
import|;
end_import

begin_comment
comment|/**  * JUnit run listener which prints full thread dump into System.err  * in case a test is failed due to timeout.  */
end_comment

begin_class
DECL|class|TimedOutTestsListener
specifier|public
class|class
name|TimedOutTestsListener
extends|extends
name|RunListener
block|{
DECL|field|TEST_TIMED_OUT_PREFIX
specifier|static
specifier|final
name|String
name|TEST_TIMED_OUT_PREFIX
init|=
literal|"test timed out after"
decl_stmt|;
DECL|field|INDENT
specifier|private
specifier|static
name|String
name|INDENT
init|=
literal|"    "
decl_stmt|;
DECL|field|output
specifier|private
specifier|final
name|PrintWriter
name|output
decl_stmt|;
DECL|method|TimedOutTestsListener ()
specifier|public
name|TimedOutTestsListener
parameter_list|()
block|{
name|this
operator|.
name|output
operator|=
operator|new
name|PrintWriter
argument_list|(
name|System
operator|.
name|err
argument_list|)
expr_stmt|;
block|}
DECL|method|TimedOutTestsListener (PrintWriter output)
specifier|public
name|TimedOutTestsListener
parameter_list|(
name|PrintWriter
name|output
parameter_list|)
block|{
name|this
operator|.
name|output
operator|=
name|output
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|testFailure (Failure failure)
specifier|public
name|void
name|testFailure
parameter_list|(
name|Failure
name|failure
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|failure
operator|!=
literal|null
operator|&&
name|failure
operator|.
name|getMessage
argument_list|()
operator|!=
literal|null
operator|&&
name|failure
operator|.
name|getMessage
argument_list|()
operator|.
name|startsWith
argument_list|(
name|TEST_TIMED_OUT_PREFIX
argument_list|)
condition|)
block|{
name|output
operator|.
name|println
argument_list|(
literal|"====> TEST TIMED OUT. PRINTING THREAD DUMP.<===="
argument_list|)
expr_stmt|;
name|output
operator|.
name|println
argument_list|()
expr_stmt|;
name|DateFormat
name|dateFormat
init|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"yyyy-MM-dd hh:mm:ss,SSS"
argument_list|)
decl_stmt|;
name|output
operator|.
name|println
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Timestamp: %s"
argument_list|,
name|dateFormat
operator|.
name|format
argument_list|(
operator|new
name|Date
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|output
operator|.
name|println
argument_list|()
expr_stmt|;
name|output
operator|.
name|println
argument_list|(
name|buildThreadDump
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|deadlocksInfo
init|=
name|buildDeadlockInfo
argument_list|()
decl_stmt|;
if|if
condition|(
name|deadlocksInfo
operator|!=
literal|null
condition|)
block|{
name|output
operator|.
name|println
argument_list|(
literal|"====> DEADLOCKS DETECTED<===="
argument_list|)
expr_stmt|;
name|output
operator|.
name|println
argument_list|()
expr_stmt|;
name|output
operator|.
name|println
argument_list|(
name|deadlocksInfo
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|buildThreadDump ()
specifier|static
name|String
name|buildThreadDump
parameter_list|()
block|{
name|StringBuilder
name|dump
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|Thread
argument_list|,
name|StackTraceElement
index|[]
argument_list|>
name|stackTraces
init|=
name|Thread
operator|.
name|getAllStackTraces
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Thread
argument_list|,
name|StackTraceElement
index|[]
argument_list|>
name|e
range|:
name|stackTraces
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Thread
name|thread
init|=
name|e
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|dump
operator|.
name|append
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"\"%s\" %s prio=%d tid=%d %s\njava.lang.Thread.State: %s"
argument_list|,
name|thread
operator|.
name|getName
argument_list|()
argument_list|,
operator|(
name|thread
operator|.
name|isDaemon
argument_list|()
condition|?
literal|"daemon"
else|:
literal|""
operator|)
argument_list|,
name|thread
operator|.
name|getPriority
argument_list|()
argument_list|,
name|thread
operator|.
name|getId
argument_list|()
argument_list|,
name|Thread
operator|.
name|State
operator|.
name|WAITING
operator|.
name|equals
argument_list|(
name|thread
operator|.
name|getState
argument_list|()
argument_list|)
condition|?
literal|"in Object.wait()"
else|:
name|thread
operator|.
name|getState
argument_list|()
operator|.
name|name
argument_list|()
operator|.
name|toLowerCase
argument_list|()
argument_list|,
name|Thread
operator|.
name|State
operator|.
name|WAITING
operator|.
name|equals
argument_list|(
name|thread
operator|.
name|getState
argument_list|()
argument_list|)
condition|?
literal|"WAITING (on object monitor)"
else|:
name|thread
operator|.
name|getState
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|StackTraceElement
name|stackTraceElement
range|:
name|e
operator|.
name|getValue
argument_list|()
control|)
block|{
name|dump
operator|.
name|append
argument_list|(
literal|"\n        at "
argument_list|)
expr_stmt|;
name|dump
operator|.
name|append
argument_list|(
name|stackTraceElement
argument_list|)
expr_stmt|;
block|}
name|dump
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
return|return
name|dump
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|buildDeadlockInfo ()
specifier|static
name|String
name|buildDeadlockInfo
parameter_list|()
block|{
name|ThreadMXBean
name|threadBean
init|=
name|ManagementFactory
operator|.
name|getThreadMXBean
argument_list|()
decl_stmt|;
name|long
index|[]
name|threadIds
init|=
name|threadBean
operator|.
name|findMonitorDeadlockedThreads
argument_list|()
decl_stmt|;
if|if
condition|(
name|threadIds
operator|!=
literal|null
operator|&&
name|threadIds
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|StringWriter
name|stringWriter
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|PrintWriter
name|out
init|=
operator|new
name|PrintWriter
argument_list|(
name|stringWriter
argument_list|)
decl_stmt|;
name|ThreadInfo
index|[]
name|infos
init|=
name|threadBean
operator|.
name|getThreadInfo
argument_list|(
name|threadIds
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
for|for
control|(
name|ThreadInfo
name|ti
range|:
name|infos
control|)
block|{
name|printThreadInfo
argument_list|(
name|ti
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|printLockInfo
argument_list|(
name|ti
operator|.
name|getLockedSynchronizers
argument_list|()
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
block|}
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|stringWriter
operator|.
name|toString
argument_list|()
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
DECL|method|printThreadInfo (ThreadInfo ti, PrintWriter out)
specifier|private
specifier|static
name|void
name|printThreadInfo
parameter_list|(
name|ThreadInfo
name|ti
parameter_list|,
name|PrintWriter
name|out
parameter_list|)
block|{
comment|// print thread information
name|printThread
argument_list|(
name|ti
argument_list|,
name|out
argument_list|)
expr_stmt|;
comment|// print stack trace with locks
name|StackTraceElement
index|[]
name|stacktrace
init|=
name|ti
operator|.
name|getStackTrace
argument_list|()
decl_stmt|;
name|MonitorInfo
index|[]
name|monitors
init|=
name|ti
operator|.
name|getLockedMonitors
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|stacktrace
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|StackTraceElement
name|ste
init|=
name|stacktrace
index|[
name|i
index|]
decl_stmt|;
name|out
operator|.
name|println
argument_list|(
name|INDENT
operator|+
literal|"at "
operator|+
name|ste
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|MonitorInfo
name|mi
range|:
name|monitors
control|)
block|{
if|if
condition|(
name|mi
operator|.
name|getLockedStackDepth
argument_list|()
operator|==
name|i
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
name|INDENT
operator|+
literal|"  - locked "
operator|+
name|mi
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
block|}
DECL|method|printThread (ThreadInfo ti, PrintWriter out)
specifier|private
specifier|static
name|void
name|printThread
parameter_list|(
name|ThreadInfo
name|ti
parameter_list|,
name|PrintWriter
name|out
parameter_list|)
block|{
name|out
operator|.
name|print
argument_list|(
literal|"\""
operator|+
name|ti
operator|.
name|getThreadName
argument_list|()
operator|+
literal|"\""
operator|+
literal|" Id="
operator|+
name|ti
operator|.
name|getThreadId
argument_list|()
operator|+
literal|" in "
operator|+
name|ti
operator|.
name|getThreadState
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|ti
operator|.
name|getLockName
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|out
operator|.
name|print
argument_list|(
literal|" on lock="
operator|+
name|ti
operator|.
name|getLockName
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|ti
operator|.
name|isSuspended
argument_list|()
condition|)
block|{
name|out
operator|.
name|print
argument_list|(
literal|" (suspended)"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|ti
operator|.
name|isInNative
argument_list|()
condition|)
block|{
name|out
operator|.
name|print
argument_list|(
literal|" (running in native)"
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
if|if
condition|(
name|ti
operator|.
name|getLockOwnerName
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
name|INDENT
operator|+
literal|" owned by "
operator|+
name|ti
operator|.
name|getLockOwnerName
argument_list|()
operator|+
literal|" Id="
operator|+
name|ti
operator|.
name|getLockOwnerId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|printLockInfo (LockInfo[] locks, PrintWriter out)
specifier|private
specifier|static
name|void
name|printLockInfo
parameter_list|(
name|LockInfo
index|[]
name|locks
parameter_list|,
name|PrintWriter
name|out
parameter_list|)
block|{
name|out
operator|.
name|println
argument_list|(
name|INDENT
operator|+
literal|"Locked synchronizers: count = "
operator|+
name|locks
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|LockInfo
name|li
range|:
name|locks
control|)
block|{
name|out
operator|.
name|println
argument_list|(
name|INDENT
operator|+
literal|"  - "
operator|+
name|li
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

