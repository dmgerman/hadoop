begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.test.system
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|test
operator|.
name|system
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
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ConcurrentModificationException
import|;
end_import

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
name|org
operator|.
name|junit
operator|.
name|Assert
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
name|conf
operator|.
name|Configuration
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
name|fs
operator|.
name|FileStatus
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
name|system
operator|.
name|process
operator|.
name|RemoteProcess
import|;
end_import

begin_comment
comment|/**  * Abstract class which encapsulates the DaemonClient which is used in the   * system tests.<br/>  *   * @param PROXY the proxy implementation of a specific Daemon   */
end_comment

begin_class
DECL|class|AbstractDaemonClient
specifier|public
specifier|abstract
class|class
name|AbstractDaemonClient
parameter_list|<
name|PROXY
extends|extends
name|DaemonProtocol
parameter_list|>
block|{
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|process
specifier|private
name|RemoteProcess
name|process
decl_stmt|;
DECL|field|connected
specifier|private
name|boolean
name|connected
decl_stmt|;
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
name|AbstractDaemonClient
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Create a Daemon client.<br/>    *     * @param conf client to be used by proxy to connect to Daemon.    * @param process the Daemon process to manage the particular daemon.    *     * @throws IOException    */
DECL|method|AbstractDaemonClient (Configuration conf, RemoteProcess process)
specifier|public
name|AbstractDaemonClient
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|RemoteProcess
name|process
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|this
operator|.
name|process
operator|=
name|process
expr_stmt|;
block|}
comment|/**    * Gets if the client is connected to the Daemon<br/>    *     * @return true if connected.    */
DECL|method|isConnected ()
specifier|public
name|boolean
name|isConnected
parameter_list|()
block|{
return|return
name|connected
return|;
block|}
DECL|method|setConnected (boolean connected)
specifier|protected
name|void
name|setConnected
parameter_list|(
name|boolean
name|connected
parameter_list|)
block|{
name|this
operator|.
name|connected
operator|=
name|connected
expr_stmt|;
block|}
comment|/**    * Create an RPC proxy to the daemon<br/>    *     * @throws IOException    */
DECL|method|connect ()
specifier|public
specifier|abstract
name|void
name|connect
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Disconnect the underlying RPC proxy to the daemon.<br/>    * @throws IOException    */
DECL|method|disconnect ()
specifier|public
specifier|abstract
name|void
name|disconnect
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Get the proxy to connect to a particular service Daemon.<br/>    *     * @return proxy to connect to a particular service Daemon.    */
DECL|method|getProxy ()
specifier|protected
specifier|abstract
name|PROXY
name|getProxy
parameter_list|()
function_decl|;
comment|/**    * Gets the daemon level configuration.<br/>    *     * @return configuration using which daemon is running    */
DECL|method|getConf ()
specifier|public
name|Configuration
name|getConf
parameter_list|()
block|{
return|return
name|conf
return|;
block|}
comment|/**    * Gets the host on which Daemon is currently running.<br/>    *     * @return hostname    */
DECL|method|getHostName ()
specifier|public
name|String
name|getHostName
parameter_list|()
block|{
return|return
name|process
operator|.
name|getHostName
argument_list|()
return|;
block|}
comment|/**    * Gets if the Daemon is ready to accept RPC connections.<br/>    *     * @return true if daemon is ready.    * @throws IOException    */
DECL|method|isReady ()
specifier|public
name|boolean
name|isReady
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|getProxy
argument_list|()
operator|.
name|isReady
argument_list|()
return|;
block|}
comment|/**    * Kills the Daemon process<br/>    * @throws IOException    */
DECL|method|kill ()
specifier|public
name|void
name|kill
parameter_list|()
throws|throws
name|IOException
block|{
name|process
operator|.
name|kill
argument_list|()
expr_stmt|;
block|}
comment|/**    * Checks if the Daemon process is alive or not<br/>    *     * @throws IOException    */
DECL|method|ping ()
specifier|public
name|void
name|ping
parameter_list|()
throws|throws
name|IOException
block|{
name|getProxy
argument_list|()
operator|.
name|ping
argument_list|()
expr_stmt|;
block|}
comment|/**    * Start up the Daemon process.<br/>    * @throws IOException    */
DECL|method|start ()
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|IOException
block|{
name|process
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
comment|/**    * Get system level view of the Daemon process.    *     * @return returns system level view of the Daemon process.    *     * @throws IOException    */
DECL|method|getProcessInfo ()
specifier|public
name|ProcessInfo
name|getProcessInfo
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|getProxy
argument_list|()
operator|.
name|getProcessInfo
argument_list|()
return|;
block|}
comment|/**    * Return a file status object that represents the path.    * @param path    *          given path    * @param local    *          whether the path is local or not    * @return a FileStatus object    * @throws java.io.FileNotFoundException when the path does not exist;    *         IOException see specific implementation    */
DECL|method|getFileStatus (String path, boolean local)
specifier|public
name|FileStatus
name|getFileStatus
parameter_list|(
name|String
name|path
parameter_list|,
name|boolean
name|local
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getProxy
argument_list|()
operator|.
name|getFileStatus
argument_list|(
name|path
argument_list|,
name|local
argument_list|)
return|;
block|}
comment|/**    * List the statuses of the files/directories in the given path if the path is    * a directory.    *     * @param path    *          given path    * @param local    *          whether the path is local or not    * @return the statuses of the files/directories in the given patch    * @throws IOException    */
DECL|method|listStatus (String path, boolean local)
specifier|public
name|FileStatus
index|[]
name|listStatus
parameter_list|(
name|String
name|path
parameter_list|,
name|boolean
name|local
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getProxy
argument_list|()
operator|.
name|listStatus
argument_list|(
name|path
argument_list|,
name|local
argument_list|)
return|;
block|}
comment|/**    * List the statuses of the files/directories in the given path if the path is    * a directory recursive/nonrecursively depending on parameters    *     * @param path    *          given path    * @param local    *          whether the path is local or not    * @param recursive     *          whether to recursively get the status    * @return the statuses of the files/directories in the given patch    * @throws IOException    */
DECL|method|listStatus (String path, boolean local, boolean recursive)
specifier|public
name|FileStatus
index|[]
name|listStatus
parameter_list|(
name|String
name|path
parameter_list|,
name|boolean
name|local
parameter_list|,
name|boolean
name|recursive
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|FileStatus
argument_list|>
name|status
init|=
operator|new
name|ArrayList
argument_list|<
name|FileStatus
argument_list|>
argument_list|()
decl_stmt|;
name|addStatus
argument_list|(
name|status
argument_list|,
name|path
argument_list|,
name|local
argument_list|,
name|recursive
argument_list|)
expr_stmt|;
return|return
name|status
operator|.
name|toArray
argument_list|(
operator|new
name|FileStatus
index|[
literal|0
index|]
argument_list|)
return|;
block|}
DECL|method|addStatus (List<FileStatus> status, String f, boolean local, boolean recursive)
specifier|private
name|void
name|addStatus
parameter_list|(
name|List
argument_list|<
name|FileStatus
argument_list|>
name|status
parameter_list|,
name|String
name|f
parameter_list|,
name|boolean
name|local
parameter_list|,
name|boolean
name|recursive
parameter_list|)
throws|throws
name|IOException
block|{
name|FileStatus
index|[]
name|fs
init|=
name|listStatus
argument_list|(
name|f
argument_list|,
name|local
argument_list|)
decl_stmt|;
if|if
condition|(
name|fs
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|FileStatus
name|fileStatus
range|:
name|fs
control|)
block|{
if|if
condition|(
operator|!
name|f
operator|.
name|equals
argument_list|(
name|fileStatus
operator|.
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
condition|)
block|{
name|status
operator|.
name|add
argument_list|(
name|fileStatus
argument_list|)
expr_stmt|;
if|if
condition|(
name|recursive
condition|)
block|{
name|addStatus
argument_list|(
name|status
argument_list|,
name|fileStatus
operator|.
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|local
argument_list|,
name|recursive
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
comment|/**    * Gets number of times FATAL log messages where logged in Daemon logs.     *<br/>    * Pattern used for searching is FATAL.<br/>    * @param excludeExpList list of exception to exclude     * @return number of occurrence of fatal message.    * @throws IOException    */
DECL|method|getNumberOfFatalStatementsInLog (String [] excludeExpList)
specifier|public
name|int
name|getNumberOfFatalStatementsInLog
parameter_list|(
name|String
index|[]
name|excludeExpList
parameter_list|)
throws|throws
name|IOException
block|{
name|DaemonProtocol
name|proxy
init|=
name|getProxy
argument_list|()
decl_stmt|;
name|String
name|pattern
init|=
literal|"FATAL"
decl_stmt|;
return|return
name|proxy
operator|.
name|getNumberOfMatchesInLogFile
argument_list|(
name|pattern
argument_list|,
name|excludeExpList
argument_list|)
return|;
block|}
comment|/**    * Gets number of times ERROR log messages where logged in Daemon logs.     *<br/>    * Pattern used for searching is ERROR.<br/>    * @param excludeExpList list of exception to exclude     * @return number of occurrence of error message.    * @throws IOException    */
DECL|method|getNumberOfErrorStatementsInLog (String[] excludeExpList)
specifier|public
name|int
name|getNumberOfErrorStatementsInLog
parameter_list|(
name|String
index|[]
name|excludeExpList
parameter_list|)
throws|throws
name|IOException
block|{
name|DaemonProtocol
name|proxy
init|=
name|getProxy
argument_list|()
decl_stmt|;
name|String
name|pattern
init|=
literal|"ERROR"
decl_stmt|;
return|return
name|proxy
operator|.
name|getNumberOfMatchesInLogFile
argument_list|(
name|pattern
argument_list|,
name|excludeExpList
argument_list|)
return|;
block|}
comment|/**    * Gets number of times Warning log messages where logged in Daemon logs.     *<br/>    * Pattern used for searching is WARN.<br/>    * @param excludeExpList list of exception to exclude     * @return number of occurrence of warning message.    * @throws IOException    */
DECL|method|getNumberOfWarnStatementsInLog (String[] excludeExpList)
specifier|public
name|int
name|getNumberOfWarnStatementsInLog
parameter_list|(
name|String
index|[]
name|excludeExpList
parameter_list|)
throws|throws
name|IOException
block|{
name|DaemonProtocol
name|proxy
init|=
name|getProxy
argument_list|()
decl_stmt|;
name|String
name|pattern
init|=
literal|"WARN"
decl_stmt|;
return|return
name|proxy
operator|.
name|getNumberOfMatchesInLogFile
argument_list|(
name|pattern
argument_list|,
name|excludeExpList
argument_list|)
return|;
block|}
comment|/**    * Gets number of time given Exception were present in log file.<br/>    *     * @param e exception class.    * @param excludeExpList list of exceptions to exclude.     * @return number of exceptions in log    * @throws IOException    */
DECL|method|getNumberOfExceptionsInLog (Exception e, String[] excludeExpList)
specifier|public
name|int
name|getNumberOfExceptionsInLog
parameter_list|(
name|Exception
name|e
parameter_list|,
name|String
index|[]
name|excludeExpList
parameter_list|)
throws|throws
name|IOException
block|{
name|DaemonProtocol
name|proxy
init|=
name|getProxy
argument_list|()
decl_stmt|;
name|String
name|pattern
init|=
name|e
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
decl_stmt|;
return|return
name|proxy
operator|.
name|getNumberOfMatchesInLogFile
argument_list|(
name|pattern
argument_list|,
name|excludeExpList
argument_list|)
return|;
block|}
comment|/**    * Number of times ConcurrentModificationException present in log file.     *<br/>    * @param excludeExpList list of exceptions to exclude.    * @return number of times exception in log file.    * @throws IOException    */
DECL|method|getNumberOfConcurrentModificationExceptionsInLog ( String[] excludeExpList)
specifier|public
name|int
name|getNumberOfConcurrentModificationExceptionsInLog
parameter_list|(
name|String
index|[]
name|excludeExpList
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getNumberOfExceptionsInLog
argument_list|(
operator|new
name|ConcurrentModificationException
argument_list|()
argument_list|,
name|excludeExpList
argument_list|)
return|;
block|}
DECL|field|errorCount
specifier|private
name|int
name|errorCount
decl_stmt|;
DECL|field|fatalCount
specifier|private
name|int
name|fatalCount
decl_stmt|;
DECL|field|concurrentExceptionCount
specifier|private
name|int
name|concurrentExceptionCount
decl_stmt|;
comment|/**    * Populate the initial exception counts to be used to assert once a testcase    * is done there was no exception in the daemon when testcase was run.    * @param excludeExpList list of exceptions to exclude    * @throws IOException    */
DECL|method|populateExceptionCount (String [] excludeExpList)
specifier|protected
name|void
name|populateExceptionCount
parameter_list|(
name|String
index|[]
name|excludeExpList
parameter_list|)
throws|throws
name|IOException
block|{
name|errorCount
operator|=
name|getNumberOfErrorStatementsInLog
argument_list|(
name|excludeExpList
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Number of error messages in logs : "
operator|+
name|errorCount
argument_list|)
expr_stmt|;
name|fatalCount
operator|=
name|getNumberOfFatalStatementsInLog
argument_list|(
name|excludeExpList
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Number of fatal statement in logs : "
operator|+
name|fatalCount
argument_list|)
expr_stmt|;
name|concurrentExceptionCount
operator|=
name|getNumberOfConcurrentModificationExceptionsInLog
argument_list|(
name|excludeExpList
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Number of concurrent modification in logs : "
operator|+
name|concurrentExceptionCount
argument_list|)
expr_stmt|;
block|}
comment|/**    * Assert if the new exceptions were logged into the log file.    *<br/>    *<b><i>    * Pre-req for the method is that populateExceptionCount() has     * to be called before calling this method.</b></i>    * @param excludeExpList list of exceptions to exclude    * @throws IOException    */
DECL|method|assertNoExceptionsOccurred (String [] excludeExpList)
specifier|protected
name|void
name|assertNoExceptionsOccurred
parameter_list|(
name|String
index|[]
name|excludeExpList
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|newerrorCount
init|=
name|getNumberOfErrorStatementsInLog
argument_list|(
name|excludeExpList
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Number of error messages while asserting :"
operator|+
name|newerrorCount
argument_list|)
expr_stmt|;
name|int
name|newfatalCount
init|=
name|getNumberOfFatalStatementsInLog
argument_list|(
name|excludeExpList
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Number of fatal messages while asserting : "
operator|+
name|newfatalCount
argument_list|)
expr_stmt|;
name|int
name|newconcurrentExceptionCount
init|=
name|getNumberOfConcurrentModificationExceptionsInLog
argument_list|(
name|excludeExpList
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Number of concurrentmodification exception while asserting :"
operator|+
name|newconcurrentExceptionCount
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"New Error Messages logged in the log file"
argument_list|,
name|errorCount
argument_list|,
name|newerrorCount
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"New Fatal messages logged in the log file"
argument_list|,
name|fatalCount
argument_list|,
name|newfatalCount
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"New ConcurrentModificationException in log file"
argument_list|,
name|concurrentExceptionCount
argument_list|,
name|newconcurrentExceptionCount
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

