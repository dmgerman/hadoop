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
name|fs
operator|.
name|FileSystem
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
name|Writable
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
name|VersionedProtocol
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
name|permission
operator|.
name|FsPermission
import|;
end_import

begin_comment
comment|/**  * RPC interface of a given Daemon.  */
end_comment

begin_interface
DECL|interface|DaemonProtocol
specifier|public
interface|interface
name|DaemonProtocol
extends|extends
name|VersionedProtocol
block|{
DECL|field|versionID
name|long
name|versionID
init|=
literal|1L
decl_stmt|;
comment|/**    * Returns the Daemon configuration.    * @return Configuration    * @throws IOException in case of errors    */
DECL|method|getDaemonConf ()
name|Configuration
name|getDaemonConf
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Check if the Daemon is alive.    *     * @throws IOException    *           if Daemon is unreachable.    */
DECL|method|ping ()
name|void
name|ping
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Check if the Daemon is ready to accept RPC connections.    *     * @return true if Daemon is ready to accept RPC connection.    * @throws IOException in case of errors    */
DECL|method|isReady ()
name|boolean
name|isReady
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Get system level view of the Daemon process.    *     * @return returns system level view of the Daemon process.    *     * @throws IOException in case of errors    */
DECL|method|getProcessInfo ()
name|ProcessInfo
name|getProcessInfo
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Return a file status object that represents the path.    * @param path    *          given path    * @param local    *          whether the path is local or not    * @return a FileStatus object    * @throws FileNotFoundException when the path does not exist;    *         IOException see specific implementation    */
DECL|method|getFileStatus (String path, boolean local)
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
function_decl|;
comment|/**    * Create a file with given permissions in a file system.    * @param path - source path where the file has to create.    * @param fileName - file name.    * @param permission - file permissions.    * @param local - identifying the path whether its local or not.    * @throws IOException - if an I/O error occurs.    */
DECL|method|createFile (String path, String fileName, FsPermission permission, boolean local)
name|void
name|createFile
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|fileName
parameter_list|,
name|FsPermission
name|permission
parameter_list|,
name|boolean
name|local
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Create a folder with given permissions in a file system.    * @param path - source path where the file has to be creating.    * @param folderName - folder name.    * @param permission - folder permissions.    * @param local - identifying the path whether its local or not.    * @throws IOException - if an I/O error occurs.    */
DECL|method|createFolder (String path, String folderName, FsPermission permission, boolean local)
specifier|public
name|void
name|createFolder
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|folderName
parameter_list|,
name|FsPermission
name|permission
parameter_list|,
name|boolean
name|local
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * List the statuses of the files/directories in the given path if the path is    * a directory.    *     * @param path    *          given path    * @param local    *          whether the path is local or not    * @return the statuses of the files/directories in the given patch    * @throws IOException in case of errors    */
DECL|method|listStatus (String path, boolean local)
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
function_decl|;
comment|/**    * Enables a particular control action to be performed on the Daemon<br/>    *     * @param action is a control action  to be enabled.    *     * @throws IOException in case of errors    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|sendAction (ControlAction action)
name|void
name|sendAction
parameter_list|(
name|ControlAction
name|action
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Checks if the particular control action has be delivered to the Daemon     * component<br/>    *     * @param action to be checked.    *     * @return true if action is still in waiting queue of     *          actions to be delivered.    * @throws IOException in case of errors    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|isActionPending (ControlAction action)
name|boolean
name|isActionPending
parameter_list|(
name|ControlAction
name|action
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Removes a particular control action from the list of the actions which the    * daemon maintains.<br/>    *<i><b>Not to be directly called by Test Case or clients.</b></i>    * @param action to be removed    * @throws IOException in case of errors    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|removeAction (ControlAction action)
name|void
name|removeAction
parameter_list|(
name|ControlAction
name|action
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Clears out the list of control actions on the particular daemon.    *<br/>    * @throws IOException in case of errors    */
DECL|method|clearActions ()
name|void
name|clearActions
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Gets a list of pending actions which are targeted on the specified key.     *<br/>    *<i><b>Not to be directly used by clients</b></i>    * @param key target    * @return list of actions.    * @throws IOException in case of errors    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|getActions (Writable key)
name|ControlAction
index|[]
name|getActions
parameter_list|(
name|Writable
name|key
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Gets the number of times a particular pattern has been found in the     * daemons log file.<br/>    *<b><i>Please note that search spans across all previous messages of    * Daemon, so better practice is to get previous counts before an operation    * and then re-check if the sequence of action has caused any problems</i></b>    * @param pattern to look for in the damon's log file    * @param list of exceptions to ignore    * @return number of times the pattern if found in log file.    * @throws IOException in case of errors    */
DECL|method|getNumberOfMatchesInLogFile (String pattern, String[] list)
name|int
name|getNumberOfMatchesInLogFile
parameter_list|(
name|String
name|pattern
parameter_list|,
name|String
index|[]
name|list
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Gets the user who started the particular daemon initially.<br/>    *     * @return user who started the particular daemon.    * @throws IOException in case of errors    */
DECL|method|getDaemonUser ()
name|String
name|getDaemonUser
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * It uses for suspending the process.    * @param pid process id.    * @return true if the process is suspended otherwise false.    * @throws IOException if an I/O error occurs.    */
DECL|method|suspendProcess (String pid)
name|boolean
name|suspendProcess
parameter_list|(
name|String
name|pid
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * It uses for resuming the suspended process.    * @param pid process id    * @return true if suspended process is resumed otherwise false.    * @throws IOException if an I/O error occurs.    */
DECL|method|resumeProcess (String pid)
name|boolean
name|resumeProcess
parameter_list|(
name|String
name|pid
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

