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
name|BufferedOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FilterOutputStream
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
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|BytesWritable
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
name|DataOutputBuffer
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
name|Text
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
name|io
operator|.
name|WritableUtils
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
name|InputSplit
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
name|util
operator|.
name|StringUtils
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

begin_comment
comment|/**  * This protocol is a binary implementation of the Pipes protocol.  */
end_comment

begin_class
DECL|class|BinaryProtocol
class|class
name|BinaryProtocol
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
implements|implements
name|DownwardProtocol
argument_list|<
name|K1
argument_list|,
name|V1
argument_list|>
block|{
DECL|field|CURRENT_PROTOCOL_VERSION
specifier|public
specifier|static
specifier|final
name|int
name|CURRENT_PROTOCOL_VERSION
init|=
literal|0
decl_stmt|;
comment|/**    * The buffer size for the command socket    */
DECL|field|BUFFER_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|BUFFER_SIZE
init|=
literal|128
operator|*
literal|1024
decl_stmt|;
DECL|field|stream
specifier|private
name|DataOutputStream
name|stream
decl_stmt|;
DECL|field|buffer
specifier|private
name|DataOutputBuffer
name|buffer
init|=
operator|new
name|DataOutputBuffer
argument_list|()
decl_stmt|;
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
name|BinaryProtocol
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|uplink
specifier|private
name|UplinkReaderThread
name|uplink
decl_stmt|;
comment|/**    * The integer codes to represent the different messages. These must match    * the C++ codes or massive confusion will result.    */
DECL|enum|MessageType
DECL|enumConstant|START
specifier|private
enum|enum
name|MessageType
block|{
name|START
argument_list|(
literal|0
argument_list|)
block|,
DECL|enumConstant|SET_JOB_CONF
name|SET_JOB_CONF
argument_list|(
literal|1
argument_list|)
block|,
DECL|enumConstant|SET_INPUT_TYPES
name|SET_INPUT_TYPES
argument_list|(
literal|2
argument_list|)
block|,
DECL|enumConstant|RUN_MAP
name|RUN_MAP
argument_list|(
literal|3
argument_list|)
block|,
DECL|enumConstant|MAP_ITEM
name|MAP_ITEM
argument_list|(
literal|4
argument_list|)
block|,
DECL|enumConstant|RUN_REDUCE
name|RUN_REDUCE
argument_list|(
literal|5
argument_list|)
block|,
DECL|enumConstant|REDUCE_KEY
name|REDUCE_KEY
argument_list|(
literal|6
argument_list|)
block|,
DECL|enumConstant|REDUCE_VALUE
name|REDUCE_VALUE
argument_list|(
literal|7
argument_list|)
block|,
DECL|enumConstant|CLOSE
name|CLOSE
argument_list|(
literal|8
argument_list|)
block|,
DECL|enumConstant|ABORT
name|ABORT
argument_list|(
literal|9
argument_list|)
block|,
DECL|enumConstant|AUTHENTICATION_REQ
name|AUTHENTICATION_REQ
argument_list|(
literal|10
argument_list|)
block|,
DECL|enumConstant|OUTPUT
name|OUTPUT
argument_list|(
literal|50
argument_list|)
block|,
DECL|enumConstant|PARTITIONED_OUTPUT
name|PARTITIONED_OUTPUT
argument_list|(
literal|51
argument_list|)
block|,
DECL|enumConstant|STATUS
name|STATUS
argument_list|(
literal|52
argument_list|)
block|,
DECL|enumConstant|PROGRESS
name|PROGRESS
argument_list|(
literal|53
argument_list|)
block|,
DECL|enumConstant|DONE
name|DONE
argument_list|(
literal|54
argument_list|)
block|,
DECL|enumConstant|REGISTER_COUNTER
name|REGISTER_COUNTER
argument_list|(
literal|55
argument_list|)
block|,
DECL|enumConstant|INCREMENT_COUNTER
name|INCREMENT_COUNTER
argument_list|(
literal|56
argument_list|)
block|,
DECL|enumConstant|AUTHENTICATION_RESP
name|AUTHENTICATION_RESP
argument_list|(
literal|57
argument_list|)
block|;
DECL|field|code
specifier|final
name|int
name|code
decl_stmt|;
DECL|method|MessageType (int code)
name|MessageType
parameter_list|(
name|int
name|code
parameter_list|)
block|{
name|this
operator|.
name|code
operator|=
name|code
expr_stmt|;
block|}
block|}
DECL|class|UplinkReaderThread
specifier|private
specifier|static
class|class
name|UplinkReaderThread
parameter_list|<
name|K2
extends|extends
name|WritableComparable
parameter_list|,
name|V2
extends|extends
name|Writable
parameter_list|>
extends|extends
name|Thread
block|{
DECL|field|inStream
specifier|private
name|DataInputStream
name|inStream
decl_stmt|;
DECL|field|handler
specifier|private
name|UpwardProtocol
argument_list|<
name|K2
argument_list|,
name|V2
argument_list|>
name|handler
decl_stmt|;
DECL|field|key
specifier|private
name|K2
name|key
decl_stmt|;
DECL|field|value
specifier|private
name|V2
name|value
decl_stmt|;
DECL|field|authPending
specifier|private
name|boolean
name|authPending
init|=
literal|true
decl_stmt|;
DECL|method|UplinkReaderThread (InputStream stream, UpwardProtocol<K2, V2> handler, K2 key, V2 value)
specifier|public
name|UplinkReaderThread
parameter_list|(
name|InputStream
name|stream
parameter_list|,
name|UpwardProtocol
argument_list|<
name|K2
argument_list|,
name|V2
argument_list|>
name|handler
parameter_list|,
name|K2
name|key
parameter_list|,
name|V2
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|inStream
operator|=
operator|new
name|DataInputStream
argument_list|(
operator|new
name|BufferedInputStream
argument_list|(
name|stream
argument_list|,
name|BUFFER_SIZE
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|handler
operator|=
name|handler
expr_stmt|;
name|this
operator|.
name|key
operator|=
name|key
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
DECL|method|closeConnection ()
specifier|public
name|void
name|closeConnection
parameter_list|()
throws|throws
name|IOException
block|{
name|inStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
while|while
condition|(
literal|true
condition|)
block|{
try|try
block|{
if|if
condition|(
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|isInterrupted
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|InterruptedException
argument_list|()
throw|;
block|}
name|int
name|cmd
init|=
name|WritableUtils
operator|.
name|readVInt
argument_list|(
name|inStream
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Handling uplink command "
operator|+
name|cmd
argument_list|)
expr_stmt|;
if|if
condition|(
name|cmd
operator|==
name|MessageType
operator|.
name|AUTHENTICATION_RESP
operator|.
name|code
condition|)
block|{
name|String
name|digest
init|=
name|Text
operator|.
name|readString
argument_list|(
name|inStream
argument_list|)
decl_stmt|;
name|authPending
operator|=
operator|!
name|handler
operator|.
name|authenticate
argument_list|(
name|digest
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|authPending
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Message "
operator|+
name|cmd
operator|+
literal|" received before authentication is "
operator|+
literal|"complete. Ignoring"
argument_list|)
expr_stmt|;
continue|continue;
block|}
elseif|else
if|if
condition|(
name|cmd
operator|==
name|MessageType
operator|.
name|OUTPUT
operator|.
name|code
condition|)
block|{
name|readObject
argument_list|(
name|key
argument_list|)
expr_stmt|;
name|readObject
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|handler
operator|.
name|output
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|cmd
operator|==
name|MessageType
operator|.
name|PARTITIONED_OUTPUT
operator|.
name|code
condition|)
block|{
name|int
name|part
init|=
name|WritableUtils
operator|.
name|readVInt
argument_list|(
name|inStream
argument_list|)
decl_stmt|;
name|readObject
argument_list|(
name|key
argument_list|)
expr_stmt|;
name|readObject
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|handler
operator|.
name|partitionedOutput
argument_list|(
name|part
argument_list|,
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|cmd
operator|==
name|MessageType
operator|.
name|STATUS
operator|.
name|code
condition|)
block|{
name|handler
operator|.
name|status
argument_list|(
name|Text
operator|.
name|readString
argument_list|(
name|inStream
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|cmd
operator|==
name|MessageType
operator|.
name|PROGRESS
operator|.
name|code
condition|)
block|{
name|handler
operator|.
name|progress
argument_list|(
name|inStream
operator|.
name|readFloat
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|cmd
operator|==
name|MessageType
operator|.
name|REGISTER_COUNTER
operator|.
name|code
condition|)
block|{
name|int
name|id
init|=
name|WritableUtils
operator|.
name|readVInt
argument_list|(
name|inStream
argument_list|)
decl_stmt|;
name|String
name|group
init|=
name|Text
operator|.
name|readString
argument_list|(
name|inStream
argument_list|)
decl_stmt|;
name|String
name|name
init|=
name|Text
operator|.
name|readString
argument_list|(
name|inStream
argument_list|)
decl_stmt|;
name|handler
operator|.
name|registerCounter
argument_list|(
name|id
argument_list|,
name|group
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|cmd
operator|==
name|MessageType
operator|.
name|INCREMENT_COUNTER
operator|.
name|code
condition|)
block|{
name|int
name|id
init|=
name|WritableUtils
operator|.
name|readVInt
argument_list|(
name|inStream
argument_list|)
decl_stmt|;
name|long
name|amount
init|=
name|WritableUtils
operator|.
name|readVLong
argument_list|(
name|inStream
argument_list|)
decl_stmt|;
name|handler
operator|.
name|incrementCounter
argument_list|(
name|id
argument_list|,
name|amount
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|cmd
operator|==
name|MessageType
operator|.
name|DONE
operator|.
name|code
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Pipe child done"
argument_list|)
expr_stmt|;
name|handler
operator|.
name|done
argument_list|()
expr_stmt|;
return|return;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Bad command code: "
operator|+
name|cmd
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
return|return;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
name|handler
operator|.
name|failed
argument_list|(
name|e
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
block|}
DECL|method|readObject (Writable obj)
specifier|private
name|void
name|readObject
parameter_list|(
name|Writable
name|obj
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|numBytes
init|=
name|WritableUtils
operator|.
name|readVInt
argument_list|(
name|inStream
argument_list|)
decl_stmt|;
name|byte
index|[]
name|buffer
decl_stmt|;
comment|// For BytesWritable and Text, use the specified length to set the length
comment|// this causes the "obvious" translations to work. So that if you emit
comment|// a string "abc" from C++, it shows up as "abc".
if|if
condition|(
name|obj
operator|instanceof
name|BytesWritable
condition|)
block|{
name|buffer
operator|=
operator|new
name|byte
index|[
name|numBytes
index|]
expr_stmt|;
name|inStream
operator|.
name|readFully
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
operator|(
operator|(
name|BytesWritable
operator|)
name|obj
operator|)
operator|.
name|set
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|numBytes
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|obj
operator|instanceof
name|Text
condition|)
block|{
name|buffer
operator|=
operator|new
name|byte
index|[
name|numBytes
index|]
expr_stmt|;
name|inStream
operator|.
name|readFully
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
operator|(
operator|(
name|Text
operator|)
name|obj
operator|)
operator|.
name|set
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|obj
operator|.
name|readFields
argument_list|(
name|inStream
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * An output stream that will save a copy of the data into a file.    */
DECL|class|TeeOutputStream
specifier|private
specifier|static
class|class
name|TeeOutputStream
extends|extends
name|FilterOutputStream
block|{
DECL|field|file
specifier|private
name|OutputStream
name|file
decl_stmt|;
DECL|method|TeeOutputStream (String filename, OutputStream base)
name|TeeOutputStream
parameter_list|(
name|String
name|filename
parameter_list|,
name|OutputStream
name|base
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|base
argument_list|)
expr_stmt|;
name|file
operator|=
operator|new
name|FileOutputStream
argument_list|(
name|filename
argument_list|)
expr_stmt|;
block|}
DECL|method|write (byte b[], int off, int len)
specifier|public
name|void
name|write
parameter_list|(
name|byte
name|b
index|[]
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
name|file
operator|.
name|write
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
DECL|method|write (int b)
specifier|public
name|void
name|write
parameter_list|(
name|int
name|b
parameter_list|)
throws|throws
name|IOException
block|{
name|file
operator|.
name|write
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
DECL|method|flush ()
specifier|public
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
name|file
operator|.
name|flush
argument_list|()
expr_stmt|;
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|flush
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Create a proxy object that will speak the binary protocol on a socket.    * Upward messages are passed on the specified handler and downward    * downward messages are public methods on this object.    * @param sock The socket to communicate on.    * @param handler The handler for the received messages.    * @param key The object to read keys into.    * @param value The object to read values into.    * @param config The job's configuration    * @throws IOException    */
DECL|method|BinaryProtocol (Socket sock, UpwardProtocol<K2, V2> handler, K2 key, V2 value, JobConf config)
specifier|public
name|BinaryProtocol
parameter_list|(
name|Socket
name|sock
parameter_list|,
name|UpwardProtocol
argument_list|<
name|K2
argument_list|,
name|V2
argument_list|>
name|handler
parameter_list|,
name|K2
name|key
parameter_list|,
name|V2
name|value
parameter_list|,
name|JobConf
name|config
parameter_list|)
throws|throws
name|IOException
block|{
name|OutputStream
name|raw
init|=
name|sock
operator|.
name|getOutputStream
argument_list|()
decl_stmt|;
comment|// If we are debugging, save a copy of the downlink commands to a file
if|if
condition|(
name|Submitter
operator|.
name|getKeepCommandFile
argument_list|(
name|config
argument_list|)
condition|)
block|{
name|raw
operator|=
operator|new
name|TeeOutputStream
argument_list|(
literal|"downlink.data"
argument_list|,
name|raw
argument_list|)
expr_stmt|;
block|}
name|stream
operator|=
operator|new
name|DataOutputStream
argument_list|(
operator|new
name|BufferedOutputStream
argument_list|(
name|raw
argument_list|,
name|BUFFER_SIZE
argument_list|)
argument_list|)
expr_stmt|;
name|uplink
operator|=
operator|new
name|UplinkReaderThread
argument_list|<
name|K2
argument_list|,
name|V2
argument_list|>
argument_list|(
name|sock
operator|.
name|getInputStream
argument_list|()
argument_list|,
name|handler
argument_list|,
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|uplink
operator|.
name|setName
argument_list|(
literal|"pipe-uplink-handler"
argument_list|)
expr_stmt|;
name|uplink
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
comment|/**    * Close the connection and shutdown the handler thread.    * @throws IOException    * @throws InterruptedException    */
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"closing connection"
argument_list|)
expr_stmt|;
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
name|uplink
operator|.
name|closeConnection
argument_list|()
expr_stmt|;
name|uplink
operator|.
name|interrupt
argument_list|()
expr_stmt|;
name|uplink
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
DECL|method|authenticate (String digest, String challenge)
specifier|public
name|void
name|authenticate
parameter_list|(
name|String
name|digest
parameter_list|,
name|String
name|challenge
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Sending AUTHENTICATION_REQ, digest="
operator|+
name|digest
operator|+
literal|", challenge="
operator|+
name|challenge
argument_list|)
expr_stmt|;
name|WritableUtils
operator|.
name|writeVInt
argument_list|(
name|stream
argument_list|,
name|MessageType
operator|.
name|AUTHENTICATION_REQ
operator|.
name|code
argument_list|)
expr_stmt|;
name|Text
operator|.
name|writeString
argument_list|(
name|stream
argument_list|,
name|digest
argument_list|)
expr_stmt|;
name|Text
operator|.
name|writeString
argument_list|(
name|stream
argument_list|,
name|challenge
argument_list|)
expr_stmt|;
block|}
DECL|method|start ()
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"starting downlink"
argument_list|)
expr_stmt|;
name|WritableUtils
operator|.
name|writeVInt
argument_list|(
name|stream
argument_list|,
name|MessageType
operator|.
name|START
operator|.
name|code
argument_list|)
expr_stmt|;
name|WritableUtils
operator|.
name|writeVInt
argument_list|(
name|stream
argument_list|,
name|CURRENT_PROTOCOL_VERSION
argument_list|)
expr_stmt|;
block|}
DECL|method|setJobConf (JobConf job)
specifier|public
name|void
name|setJobConf
parameter_list|(
name|JobConf
name|job
parameter_list|)
throws|throws
name|IOException
block|{
name|WritableUtils
operator|.
name|writeVInt
argument_list|(
name|stream
argument_list|,
name|MessageType
operator|.
name|SET_JOB_CONF
operator|.
name|code
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|itm
range|:
name|job
control|)
block|{
name|list
operator|.
name|add
argument_list|(
name|itm
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
name|itm
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|WritableUtils
operator|.
name|writeVInt
argument_list|(
name|stream
argument_list|,
name|list
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|entry
range|:
name|list
control|)
block|{
name|Text
operator|.
name|writeString
argument_list|(
name|stream
argument_list|,
name|entry
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|setInputTypes (String keyType, String valueType)
specifier|public
name|void
name|setInputTypes
parameter_list|(
name|String
name|keyType
parameter_list|,
name|String
name|valueType
parameter_list|)
throws|throws
name|IOException
block|{
name|WritableUtils
operator|.
name|writeVInt
argument_list|(
name|stream
argument_list|,
name|MessageType
operator|.
name|SET_INPUT_TYPES
operator|.
name|code
argument_list|)
expr_stmt|;
name|Text
operator|.
name|writeString
argument_list|(
name|stream
argument_list|,
name|keyType
argument_list|)
expr_stmt|;
name|Text
operator|.
name|writeString
argument_list|(
name|stream
argument_list|,
name|valueType
argument_list|)
expr_stmt|;
block|}
DECL|method|runMap (InputSplit split, int numReduces, boolean pipedInput)
specifier|public
name|void
name|runMap
parameter_list|(
name|InputSplit
name|split
parameter_list|,
name|int
name|numReduces
parameter_list|,
name|boolean
name|pipedInput
parameter_list|)
throws|throws
name|IOException
block|{
name|WritableUtils
operator|.
name|writeVInt
argument_list|(
name|stream
argument_list|,
name|MessageType
operator|.
name|RUN_MAP
operator|.
name|code
argument_list|)
expr_stmt|;
name|writeObject
argument_list|(
name|split
argument_list|)
expr_stmt|;
name|WritableUtils
operator|.
name|writeVInt
argument_list|(
name|stream
argument_list|,
name|numReduces
argument_list|)
expr_stmt|;
name|WritableUtils
operator|.
name|writeVInt
argument_list|(
name|stream
argument_list|,
name|pipedInput
condition|?
literal|1
else|:
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|mapItem (WritableComparable key, Writable value)
specifier|public
name|void
name|mapItem
parameter_list|(
name|WritableComparable
name|key
parameter_list|,
name|Writable
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|WritableUtils
operator|.
name|writeVInt
argument_list|(
name|stream
argument_list|,
name|MessageType
operator|.
name|MAP_ITEM
operator|.
name|code
argument_list|)
expr_stmt|;
name|writeObject
argument_list|(
name|key
argument_list|)
expr_stmt|;
name|writeObject
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
DECL|method|runReduce (int reduce, boolean pipedOutput)
specifier|public
name|void
name|runReduce
parameter_list|(
name|int
name|reduce
parameter_list|,
name|boolean
name|pipedOutput
parameter_list|)
throws|throws
name|IOException
block|{
name|WritableUtils
operator|.
name|writeVInt
argument_list|(
name|stream
argument_list|,
name|MessageType
operator|.
name|RUN_REDUCE
operator|.
name|code
argument_list|)
expr_stmt|;
name|WritableUtils
operator|.
name|writeVInt
argument_list|(
name|stream
argument_list|,
name|reduce
argument_list|)
expr_stmt|;
name|WritableUtils
operator|.
name|writeVInt
argument_list|(
name|stream
argument_list|,
name|pipedOutput
condition|?
literal|1
else|:
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|reduceKey (WritableComparable key)
specifier|public
name|void
name|reduceKey
parameter_list|(
name|WritableComparable
name|key
parameter_list|)
throws|throws
name|IOException
block|{
name|WritableUtils
operator|.
name|writeVInt
argument_list|(
name|stream
argument_list|,
name|MessageType
operator|.
name|REDUCE_KEY
operator|.
name|code
argument_list|)
expr_stmt|;
name|writeObject
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
DECL|method|reduceValue (Writable value)
specifier|public
name|void
name|reduceValue
parameter_list|(
name|Writable
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|WritableUtils
operator|.
name|writeVInt
argument_list|(
name|stream
argument_list|,
name|MessageType
operator|.
name|REDUCE_VALUE
operator|.
name|code
argument_list|)
expr_stmt|;
name|writeObject
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
DECL|method|endOfInput ()
specifier|public
name|void
name|endOfInput
parameter_list|()
throws|throws
name|IOException
block|{
name|WritableUtils
operator|.
name|writeVInt
argument_list|(
name|stream
argument_list|,
name|MessageType
operator|.
name|CLOSE
operator|.
name|code
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Sent close command"
argument_list|)
expr_stmt|;
block|}
DECL|method|abort ()
specifier|public
name|void
name|abort
parameter_list|()
throws|throws
name|IOException
block|{
name|WritableUtils
operator|.
name|writeVInt
argument_list|(
name|stream
argument_list|,
name|MessageType
operator|.
name|ABORT
operator|.
name|code
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Sent abort command"
argument_list|)
expr_stmt|;
block|}
DECL|method|flush ()
specifier|public
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
name|stream
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
comment|/**    * Write the given object to the stream. If it is a Text or BytesWritable,    * write it directly. Otherwise, write it to a buffer and then write the    * length and data to the stream.    * @param obj the object to write    * @throws IOException    */
DECL|method|writeObject (Writable obj)
specifier|private
name|void
name|writeObject
parameter_list|(
name|Writable
name|obj
parameter_list|)
throws|throws
name|IOException
block|{
comment|// For Text and BytesWritable, encode them directly, so that they end up
comment|// in C++ as the natural translations.
if|if
condition|(
name|obj
operator|instanceof
name|Text
condition|)
block|{
name|Text
name|t
init|=
operator|(
name|Text
operator|)
name|obj
decl_stmt|;
name|int
name|len
init|=
name|t
operator|.
name|getLength
argument_list|()
decl_stmt|;
name|WritableUtils
operator|.
name|writeVInt
argument_list|(
name|stream
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|stream
operator|.
name|write
argument_list|(
name|t
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|obj
operator|instanceof
name|BytesWritable
condition|)
block|{
name|BytesWritable
name|b
init|=
operator|(
name|BytesWritable
operator|)
name|obj
decl_stmt|;
name|int
name|len
init|=
name|b
operator|.
name|getLength
argument_list|()
decl_stmt|;
name|WritableUtils
operator|.
name|writeVInt
argument_list|(
name|stream
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|stream
operator|.
name|write
argument_list|(
name|b
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|buffer
operator|.
name|reset
argument_list|()
expr_stmt|;
name|obj
operator|.
name|write
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
name|int
name|length
init|=
name|buffer
operator|.
name|getLength
argument_list|()
decl_stmt|;
name|WritableUtils
operator|.
name|writeVInt
argument_list|(
name|stream
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|stream
operator|.
name|write
argument_list|(
name|buffer
operator|.
name|getData
argument_list|()
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

