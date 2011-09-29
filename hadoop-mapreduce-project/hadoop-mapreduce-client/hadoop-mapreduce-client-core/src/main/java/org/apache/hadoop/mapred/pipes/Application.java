begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred.pipes
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|pipes
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedInputStream
import|;
end_import

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
name|net
operator|.
name|Socket
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
name|HashMap
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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
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
name|javax
operator|.
name|crypto
operator|.
name|SecretKey
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
name|mapreduce
operator|.
name|MRJobConfig
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
name|mapreduce
operator|.
name|filecache
operator|.
name|DistributedCache
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
name|FSDataOutputStream
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
name|fs
operator|.
name|FileUtil
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
name|Path
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
name|FloatWritable
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
name|NullWritable
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
name|io
operator|.
name|WritableComparable
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
name|mapred
operator|.
name|JobConf
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
name|mapred
operator|.
name|OutputCollector
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
name|mapred
operator|.
name|RecordReader
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
name|mapred
operator|.
name|Reporter
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
name|mapred
operator|.
name|TaskAttemptID
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
name|mapred
operator|.
name|TaskLog
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
name|mapreduce
operator|.
name|security
operator|.
name|SecureShuffleUtils
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
name|mapreduce
operator|.
name|security
operator|.
name|TokenCache
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
name|mapreduce
operator|.
name|security
operator|.
name|token
operator|.
name|JobTokenIdentifier
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
name|mapreduce
operator|.
name|security
operator|.
name|token
operator|.
name|JobTokenSecretManager
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
name|Token
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
name|ReflectionUtils
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

begin_comment
comment|/**  * This class is responsible for launching and communicating with the child   * process.  */
end_comment

begin_class
DECL|class|Application
class|class
name|Application
parameter_list|<
name|K1
extends|extends
name|WritableComparable
parameter_list|,
name|V1
extends|extends
name|Writable
parameter_list|,
name|K2
extends|extends
name|WritableComparable
parameter_list|,
name|V2
extends|extends
name|Writable
parameter_list|>
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
name|Application
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|serverSocket
specifier|private
name|ServerSocket
name|serverSocket
decl_stmt|;
DECL|field|process
specifier|private
name|Process
name|process
decl_stmt|;
DECL|field|clientSocket
specifier|private
name|Socket
name|clientSocket
decl_stmt|;
DECL|field|handler
specifier|private
name|OutputHandler
argument_list|<
name|K2
argument_list|,
name|V2
argument_list|>
name|handler
decl_stmt|;
DECL|field|downlink
specifier|private
name|DownwardProtocol
argument_list|<
name|K1
argument_list|,
name|V1
argument_list|>
name|downlink
decl_stmt|;
DECL|field|WINDOWS
specifier|static
specifier|final
name|boolean
name|WINDOWS
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"os.name"
argument_list|)
operator|.
name|startsWith
argument_list|(
literal|"Windows"
argument_list|)
decl_stmt|;
comment|/**    * Start the child process to handle the task for us.    * @param conf the task's configuration    * @param recordReader the fake record reader to update progress with    * @param output the collector to send output to    * @param reporter the reporter for the task    * @param outputKeyClass the class of the output keys    * @param outputValueClass the class of the output values    * @throws IOException    * @throws InterruptedException    */
DECL|method|Application (JobConf conf, RecordReader<FloatWritable, NullWritable> recordReader, OutputCollector<K2,V2> output, Reporter reporter, Class<? extends K2> outputKeyClass, Class<? extends V2> outputValueClass )
name|Application
parameter_list|(
name|JobConf
name|conf
parameter_list|,
name|RecordReader
argument_list|<
name|FloatWritable
argument_list|,
name|NullWritable
argument_list|>
name|recordReader
parameter_list|,
name|OutputCollector
argument_list|<
name|K2
argument_list|,
name|V2
argument_list|>
name|output
parameter_list|,
name|Reporter
name|reporter
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|K2
argument_list|>
name|outputKeyClass
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|V2
argument_list|>
name|outputValueClass
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|serverSocket
operator|=
operator|new
name|ServerSocket
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|env
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
comment|// add TMPDIR environment variable with the value of java.io.tmpdir
name|env
operator|.
name|put
argument_list|(
literal|"TMPDIR"
argument_list|,
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.io.tmpdir"
argument_list|)
argument_list|)
expr_stmt|;
name|env
operator|.
name|put
argument_list|(
name|Submitter
operator|.
name|PORT
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|serverSocket
operator|.
name|getLocalPort
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|//Add token to the environment if security is enabled
name|Token
argument_list|<
name|JobTokenIdentifier
argument_list|>
name|jobToken
init|=
name|TokenCache
operator|.
name|getJobToken
argument_list|(
name|conf
operator|.
name|getCredentials
argument_list|()
argument_list|)
decl_stmt|;
comment|// This password is used as shared secret key between this application and
comment|// child pipes process
name|byte
index|[]
name|password
init|=
name|jobToken
operator|.
name|getPassword
argument_list|()
decl_stmt|;
name|String
name|localPasswordFile
init|=
operator|new
name|File
argument_list|(
literal|"."
argument_list|)
operator|+
name|Path
operator|.
name|SEPARATOR
operator|+
literal|"jobTokenPassword"
decl_stmt|;
name|writePasswordToLocalFile
argument_list|(
name|localPasswordFile
argument_list|,
name|password
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|env
operator|.
name|put
argument_list|(
literal|"hadoop.pipes.shared.secret.location"
argument_list|,
name|localPasswordFile
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|cmd
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|String
name|interpretor
init|=
name|conf
operator|.
name|get
argument_list|(
name|Submitter
operator|.
name|INTERPRETOR
argument_list|)
decl_stmt|;
if|if
condition|(
name|interpretor
operator|!=
literal|null
condition|)
block|{
name|cmd
operator|.
name|add
argument_list|(
name|interpretor
argument_list|)
expr_stmt|;
block|}
name|String
name|executable
init|=
name|DistributedCache
operator|.
name|getLocalCacheFiles
argument_list|(
name|conf
argument_list|)
index|[
literal|0
index|]
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
operator|new
name|File
argument_list|(
name|executable
argument_list|)
operator|.
name|canExecute
argument_list|()
condition|)
block|{
comment|// LinuxTaskController sets +x permissions on all distcache files already.
comment|// In case of DefaultTaskController, set permissions here.
name|FileUtil
operator|.
name|chmod
argument_list|(
name|executable
argument_list|,
literal|"u+x"
argument_list|)
expr_stmt|;
block|}
name|cmd
operator|.
name|add
argument_list|(
name|executable
argument_list|)
expr_stmt|;
comment|// wrap the command in a stdout/stderr capture
comment|// we are starting map/reduce task of the pipes job. this is not a cleanup
comment|// attempt.
name|TaskAttemptID
name|taskid
init|=
name|TaskAttemptID
operator|.
name|forName
argument_list|(
name|conf
operator|.
name|get
argument_list|(
name|MRJobConfig
operator|.
name|TASK_ATTEMPT_ID
argument_list|)
argument_list|)
decl_stmt|;
name|File
name|stdout
init|=
name|TaskLog
operator|.
name|getTaskLogFile
argument_list|(
name|taskid
argument_list|,
literal|false
argument_list|,
name|TaskLog
operator|.
name|LogName
operator|.
name|STDOUT
argument_list|)
decl_stmt|;
name|File
name|stderr
init|=
name|TaskLog
operator|.
name|getTaskLogFile
argument_list|(
name|taskid
argument_list|,
literal|false
argument_list|,
name|TaskLog
operator|.
name|LogName
operator|.
name|STDERR
argument_list|)
decl_stmt|;
name|long
name|logLength
init|=
name|TaskLog
operator|.
name|getTaskLogLength
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|cmd
operator|=
name|TaskLog
operator|.
name|captureOutAndError
argument_list|(
literal|null
argument_list|,
name|cmd
argument_list|,
name|stdout
argument_list|,
name|stderr
argument_list|,
name|logLength
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|process
operator|=
name|runClient
argument_list|(
name|cmd
argument_list|,
name|env
argument_list|)
expr_stmt|;
name|clientSocket
operator|=
name|serverSocket
operator|.
name|accept
argument_list|()
expr_stmt|;
name|String
name|challenge
init|=
name|getSecurityChallenge
argument_list|()
decl_stmt|;
name|String
name|digestToSend
init|=
name|createDigest
argument_list|(
name|password
argument_list|,
name|challenge
argument_list|)
decl_stmt|;
name|String
name|digestExpected
init|=
name|createDigest
argument_list|(
name|password
argument_list|,
name|digestToSend
argument_list|)
decl_stmt|;
name|handler
operator|=
operator|new
name|OutputHandler
argument_list|<
name|K2
argument_list|,
name|V2
argument_list|>
argument_list|(
name|output
argument_list|,
name|reporter
argument_list|,
name|recordReader
argument_list|,
name|digestExpected
argument_list|)
expr_stmt|;
name|K2
name|outputKey
init|=
operator|(
name|K2
operator|)
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|outputKeyClass
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|V2
name|outputValue
init|=
operator|(
name|V2
operator|)
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|outputValueClass
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|downlink
operator|=
operator|new
name|BinaryProtocol
argument_list|<
name|K1
argument_list|,
name|V1
argument_list|,
name|K2
argument_list|,
name|V2
argument_list|>
argument_list|(
name|clientSocket
argument_list|,
name|handler
argument_list|,
name|outputKey
argument_list|,
name|outputValue
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|downlink
operator|.
name|authenticate
argument_list|(
name|digestToSend
argument_list|,
name|challenge
argument_list|)
expr_stmt|;
name|waitForAuthentication
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Authentication succeeded"
argument_list|)
expr_stmt|;
name|downlink
operator|.
name|start
argument_list|()
expr_stmt|;
name|downlink
operator|.
name|setJobConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
DECL|method|getSecurityChallenge ()
specifier|private
name|String
name|getSecurityChallenge
parameter_list|()
block|{
name|Random
name|rand
init|=
operator|new
name|Random
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
decl_stmt|;
comment|//Use 4 random integers so as to have 16 random bytes.
name|StringBuilder
name|strBuilder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|strBuilder
operator|.
name|append
argument_list|(
name|rand
operator|.
name|nextInt
argument_list|(
literal|0x7fffffff
argument_list|)
argument_list|)
expr_stmt|;
name|strBuilder
operator|.
name|append
argument_list|(
name|rand
operator|.
name|nextInt
argument_list|(
literal|0x7fffffff
argument_list|)
argument_list|)
expr_stmt|;
name|strBuilder
operator|.
name|append
argument_list|(
name|rand
operator|.
name|nextInt
argument_list|(
literal|0x7fffffff
argument_list|)
argument_list|)
expr_stmt|;
name|strBuilder
operator|.
name|append
argument_list|(
name|rand
operator|.
name|nextInt
argument_list|(
literal|0x7fffffff
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|strBuilder
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|writePasswordToLocalFile (String localPasswordFile, byte[] password, JobConf conf)
specifier|private
name|void
name|writePasswordToLocalFile
parameter_list|(
name|String
name|localPasswordFile
parameter_list|,
name|byte
index|[]
name|password
parameter_list|,
name|JobConf
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|FileSystem
name|localFs
init|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|Path
name|localPath
init|=
operator|new
name|Path
argument_list|(
name|localPasswordFile
argument_list|)
decl_stmt|;
name|FSDataOutputStream
name|out
init|=
name|FileSystem
operator|.
name|create
argument_list|(
name|localFs
argument_list|,
name|localPath
argument_list|,
operator|new
name|FsPermission
argument_list|(
literal|"400"
argument_list|)
argument_list|)
decl_stmt|;
name|out
operator|.
name|write
argument_list|(
name|password
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Get the downward protocol object that can send commands down to the    * application.    * @return the downlink proxy    */
DECL|method|getDownlink ()
name|DownwardProtocol
argument_list|<
name|K1
argument_list|,
name|V1
argument_list|>
name|getDownlink
parameter_list|()
block|{
return|return
name|downlink
return|;
block|}
comment|/**    * Wait for authentication response.    * @throws IOException    * @throws InterruptedException    */
DECL|method|waitForAuthentication ()
name|void
name|waitForAuthentication
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|downlink
operator|.
name|flush
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Waiting for authentication response"
argument_list|)
expr_stmt|;
name|handler
operator|.
name|waitForAuthentication
argument_list|()
expr_stmt|;
block|}
comment|/**    * Wait for the application to finish    * @return did the application finish correctly?    * @throws Throwable    */
DECL|method|waitForFinish ()
name|boolean
name|waitForFinish
parameter_list|()
throws|throws
name|Throwable
block|{
name|downlink
operator|.
name|flush
argument_list|()
expr_stmt|;
return|return
name|handler
operator|.
name|waitForFinish
argument_list|()
return|;
block|}
comment|/**    * Abort the application and wait for it to finish.    * @param t the exception that signalled the problem    * @throws IOException A wrapper around the exception that was passed in    */
DECL|method|abort (Throwable t)
name|void
name|abort
parameter_list|(
name|Throwable
name|t
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Aborting because of "
operator|+
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|t
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|downlink
operator|.
name|abort
argument_list|()
expr_stmt|;
name|downlink
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// IGNORE cleanup problems
block|}
try|try
block|{
name|handler
operator|.
name|waitForFinish
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|ignored
parameter_list|)
block|{
name|process
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
name|IOException
name|wrapper
init|=
operator|new
name|IOException
argument_list|(
literal|"pipe child exception"
argument_list|)
decl_stmt|;
name|wrapper
operator|.
name|initCause
argument_list|(
name|t
argument_list|)
expr_stmt|;
throw|throw
name|wrapper
throw|;
block|}
comment|/**    * Clean up the child procress and socket.    * @throws IOException    */
DECL|method|cleanup ()
name|void
name|cleanup
parameter_list|()
throws|throws
name|IOException
block|{
name|serverSocket
operator|.
name|close
argument_list|()
expr_stmt|;
try|try
block|{
name|downlink
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Run a given command in a subprocess, including threads to copy its stdout    * and stderr to our stdout and stderr.    * @param command the command and its arguments    * @param env the environment to run the process in    * @return a handle on the process    * @throws IOException    */
DECL|method|runClient (List<String> command, Map<String, String> env)
specifier|static
name|Process
name|runClient
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|command
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|env
parameter_list|)
throws|throws
name|IOException
block|{
name|ProcessBuilder
name|builder
init|=
operator|new
name|ProcessBuilder
argument_list|(
name|command
argument_list|)
decl_stmt|;
if|if
condition|(
name|env
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|environment
argument_list|()
operator|.
name|putAll
argument_list|(
name|env
argument_list|)
expr_stmt|;
block|}
name|Process
name|result
init|=
name|builder
operator|.
name|start
argument_list|()
decl_stmt|;
return|return
name|result
return|;
block|}
DECL|method|createDigest (byte[] password, String data)
specifier|public
specifier|static
name|String
name|createDigest
parameter_list|(
name|byte
index|[]
name|password
parameter_list|,
name|String
name|data
parameter_list|)
throws|throws
name|IOException
block|{
name|SecretKey
name|key
init|=
name|JobTokenSecretManager
operator|.
name|createSecretKey
argument_list|(
name|password
argument_list|)
decl_stmt|;
return|return
name|SecureShuffleUtils
operator|.
name|hashFromString
argument_list|(
name|data
argument_list|,
name|key
argument_list|)
return|;
block|}
block|}
end_class

end_unit

