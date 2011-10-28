begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.logaggregation
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|nodemanager
operator|.
name|containermanager
operator|.
name|logaggregation
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInput
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
name|DataOutput
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
name|EOFException
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
name|FileInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
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
name|Writer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PrivilegedExceptionAction
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EnumSet
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|input
operator|.
name|BoundedInputStream
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
name|CreateFlag
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
name|FSDataInputStream
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
name|FileContext
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
name|Options
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
name|file
operator|.
name|tfile
operator|.
name|TFile
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
name|UserGroupInformation
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
name|yarn
operator|.
name|YarnException
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ApplicationAccessType
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ContainerId
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
name|yarn
operator|.
name|conf
operator|.
name|YarnConfiguration
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
name|yarn
operator|.
name|util
operator|.
name|ConverterUtils
import|;
end_import

begin_class
DECL|class|AggregatedLogFormat
specifier|public
class|class
name|AggregatedLogFormat
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|AggregatedLogFormat
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|APPLICATION_ACL_KEY
specifier|private
specifier|static
specifier|final
name|LogKey
name|APPLICATION_ACL_KEY
init|=
operator|new
name|LogKey
argument_list|(
literal|"APPLICATION_ACL"
argument_list|)
decl_stmt|;
DECL|field|APPLICATION_OWNER_KEY
specifier|private
specifier|static
specifier|final
name|LogKey
name|APPLICATION_OWNER_KEY
init|=
operator|new
name|LogKey
argument_list|(
literal|"APPLICATION_OWNER"
argument_list|)
decl_stmt|;
DECL|field|VERSION_KEY
specifier|private
specifier|static
specifier|final
name|LogKey
name|VERSION_KEY
init|=
operator|new
name|LogKey
argument_list|(
literal|"VERSION"
argument_list|)
decl_stmt|;
DECL|field|RESERVED_KEYS
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|LogKey
argument_list|>
name|RESERVED_KEYS
decl_stmt|;
comment|//Maybe write out the retention policy.
comment|//Maybe write out a list of containerLogs skipped by the retention policy.
DECL|field|VERSION
specifier|private
specifier|static
specifier|final
name|int
name|VERSION
init|=
literal|1
decl_stmt|;
static|static
block|{
name|RESERVED_KEYS
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|AggregatedLogFormat
operator|.
name|LogKey
argument_list|>
argument_list|()
expr_stmt|;
name|RESERVED_KEYS
operator|.
name|put
argument_list|(
name|APPLICATION_ACL_KEY
operator|.
name|toString
argument_list|()
argument_list|,
name|APPLICATION_ACL_KEY
argument_list|)
expr_stmt|;
name|RESERVED_KEYS
operator|.
name|put
argument_list|(
name|APPLICATION_OWNER_KEY
operator|.
name|toString
argument_list|()
argument_list|,
name|APPLICATION_OWNER_KEY
argument_list|)
expr_stmt|;
name|RESERVED_KEYS
operator|.
name|put
argument_list|(
name|VERSION_KEY
operator|.
name|toString
argument_list|()
argument_list|,
name|VERSION_KEY
argument_list|)
expr_stmt|;
block|}
DECL|class|LogKey
specifier|public
specifier|static
class|class
name|LogKey
implements|implements
name|Writable
block|{
DECL|field|keyString
specifier|private
name|String
name|keyString
decl_stmt|;
DECL|method|LogKey ()
specifier|public
name|LogKey
parameter_list|()
block|{      }
DECL|method|LogKey (ContainerId containerId)
specifier|public
name|LogKey
parameter_list|(
name|ContainerId
name|containerId
parameter_list|)
block|{
name|this
operator|.
name|keyString
operator|=
name|containerId
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
DECL|method|LogKey (String keyString)
specifier|public
name|LogKey
parameter_list|(
name|String
name|keyString
parameter_list|)
block|{
name|this
operator|.
name|keyString
operator|=
name|keyString
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|write (DataOutput out)
specifier|public
name|void
name|write
parameter_list|(
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeUTF
argument_list|(
name|this
operator|.
name|keyString
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|readFields (DataInput in)
specifier|public
name|void
name|readFields
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|keyString
operator|=
name|in
operator|.
name|readUTF
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|this
operator|.
name|keyString
return|;
block|}
block|}
DECL|class|LogValue
specifier|public
specifier|static
class|class
name|LogValue
block|{
DECL|field|rootLogDirs
specifier|private
specifier|final
name|String
index|[]
name|rootLogDirs
decl_stmt|;
DECL|field|containerId
specifier|private
specifier|final
name|ContainerId
name|containerId
decl_stmt|;
comment|// TODO Maybe add a version string here. Instead of changing the version of
comment|// the entire k-v format
DECL|method|LogValue (String[] rootLogDirs, ContainerId containerId)
specifier|public
name|LogValue
parameter_list|(
name|String
index|[]
name|rootLogDirs
parameter_list|,
name|ContainerId
name|containerId
parameter_list|)
block|{
name|this
operator|.
name|rootLogDirs
operator|=
name|rootLogDirs
expr_stmt|;
name|this
operator|.
name|containerId
operator|=
name|containerId
expr_stmt|;
block|}
DECL|method|write (DataOutputStream out)
specifier|public
name|void
name|write
parameter_list|(
name|DataOutputStream
name|out
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|String
name|rootLogDir
range|:
name|this
operator|.
name|rootLogDirs
control|)
block|{
name|File
name|appLogDir
init|=
operator|new
name|File
argument_list|(
name|rootLogDir
argument_list|,
name|ConverterUtils
operator|.
name|toString
argument_list|(
name|this
operator|.
name|containerId
operator|.
name|getApplicationAttemptId
argument_list|()
operator|.
name|getApplicationId
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|File
name|containerLogDir
init|=
operator|new
name|File
argument_list|(
name|appLogDir
argument_list|,
name|ConverterUtils
operator|.
name|toString
argument_list|(
name|this
operator|.
name|containerId
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|containerLogDir
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
continue|continue;
comment|// ContainerDir may have been deleted by the user.
block|}
for|for
control|(
name|File
name|logFile
range|:
name|containerLogDir
operator|.
name|listFiles
argument_list|()
control|)
block|{
comment|// Write the logFile Type
name|out
operator|.
name|writeUTF
argument_list|(
name|logFile
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
comment|// Write the log length as UTF so that it is printable
name|out
operator|.
name|writeUTF
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|logFile
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// Write the log itself
name|FileInputStream
name|in
init|=
literal|null
decl_stmt|;
try|try
block|{
name|in
operator|=
operator|new
name|FileInputStream
argument_list|(
name|logFile
argument_list|)
expr_stmt|;
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
literal|65535
index|]
decl_stmt|;
name|int
name|len
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|(
name|len
operator|=
name|in
operator|.
name|read
argument_list|(
name|buf
argument_list|)
operator|)
operator|!=
operator|-
literal|1
condition|)
block|{
name|out
operator|.
name|write
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
DECL|class|LogWriter
specifier|public
specifier|static
class|class
name|LogWriter
block|{
DECL|field|fsDataOStream
specifier|private
specifier|final
name|FSDataOutputStream
name|fsDataOStream
decl_stmt|;
DECL|field|writer
specifier|private
specifier|final
name|TFile
operator|.
name|Writer
name|writer
decl_stmt|;
DECL|method|LogWriter (final Configuration conf, final Path remoteAppLogFile, UserGroupInformation userUgi)
specifier|public
name|LogWriter
parameter_list|(
specifier|final
name|Configuration
name|conf
parameter_list|,
specifier|final
name|Path
name|remoteAppLogFile
parameter_list|,
name|UserGroupInformation
name|userUgi
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|this
operator|.
name|fsDataOStream
operator|=
name|userUgi
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|FSDataOutputStream
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|FSDataOutputStream
name|run
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|FileContext
operator|.
name|getFileContext
argument_list|(
name|conf
argument_list|)
operator|.
name|create
argument_list|(
name|remoteAppLogFile
argument_list|,
name|EnumSet
operator|.
name|of
argument_list|(
name|CreateFlag
operator|.
name|CREATE
argument_list|,
name|CreateFlag
operator|.
name|OVERWRITE
argument_list|)
argument_list|,
operator|new
name|Options
operator|.
name|CreateOpts
index|[]
block|{}
argument_list|)
return|;
block|}
block|}
argument_list|)
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
argument_list|)
throw|;
block|}
comment|// Keys are not sorted: null arg
comment|// 256KB minBlockSize : Expected log size for each container too
name|this
operator|.
name|writer
operator|=
operator|new
name|TFile
operator|.
name|Writer
argument_list|(
name|this
operator|.
name|fsDataOStream
argument_list|,
literal|256
operator|*
literal|1024
argument_list|,
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|NM_LOG_AGG_COMPRESSION_TYPE
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_NM_LOG_AGG_COMPRESSION_TYPE
argument_list|)
argument_list|,
literal|null
argument_list|,
name|conf
argument_list|)
expr_stmt|;
comment|//Write the version string
name|writeVersion
argument_list|()
expr_stmt|;
block|}
DECL|method|writeVersion ()
specifier|private
name|void
name|writeVersion
parameter_list|()
throws|throws
name|IOException
block|{
name|DataOutputStream
name|out
init|=
name|this
operator|.
name|writer
operator|.
name|prepareAppendKey
argument_list|(
operator|-
literal|1
argument_list|)
decl_stmt|;
name|VERSION_KEY
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|out
operator|=
name|this
operator|.
name|writer
operator|.
name|prepareAppendValue
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|VERSION
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|this
operator|.
name|fsDataOStream
operator|.
name|hflush
argument_list|()
expr_stmt|;
block|}
DECL|method|writeApplicationOwner (String user)
specifier|public
name|void
name|writeApplicationOwner
parameter_list|(
name|String
name|user
parameter_list|)
throws|throws
name|IOException
block|{
name|DataOutputStream
name|out
init|=
name|this
operator|.
name|writer
operator|.
name|prepareAppendKey
argument_list|(
operator|-
literal|1
argument_list|)
decl_stmt|;
name|APPLICATION_OWNER_KEY
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|out
operator|=
name|this
operator|.
name|writer
operator|.
name|prepareAppendValue
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeUTF
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|writeApplicationACLs (Map<ApplicationAccessType, String> appAcls)
specifier|public
name|void
name|writeApplicationACLs
parameter_list|(
name|Map
argument_list|<
name|ApplicationAccessType
argument_list|,
name|String
argument_list|>
name|appAcls
parameter_list|)
throws|throws
name|IOException
block|{
name|DataOutputStream
name|out
init|=
name|this
operator|.
name|writer
operator|.
name|prepareAppendKey
argument_list|(
operator|-
literal|1
argument_list|)
decl_stmt|;
name|APPLICATION_ACL_KEY
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|out
operator|=
name|this
operator|.
name|writer
operator|.
name|prepareAppendValue
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|ApplicationAccessType
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|appAcls
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|out
operator|.
name|writeUTF
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeUTF
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|append (LogKey logKey, LogValue logValue)
specifier|public
name|void
name|append
parameter_list|(
name|LogKey
name|logKey
parameter_list|,
name|LogValue
name|logValue
parameter_list|)
throws|throws
name|IOException
block|{
name|DataOutputStream
name|out
init|=
name|this
operator|.
name|writer
operator|.
name|prepareAppendKey
argument_list|(
operator|-
literal|1
argument_list|)
decl_stmt|;
name|logKey
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|out
operator|=
name|this
operator|.
name|writer
operator|.
name|prepareAppendValue
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
name|logValue
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|this
operator|.
name|fsDataOStream
operator|.
name|hflush
argument_list|()
expr_stmt|;
block|}
DECL|method|closeWriter ()
specifier|public
name|void
name|closeWriter
parameter_list|()
block|{
try|try
block|{
name|this
operator|.
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Exception closing writer"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|this
operator|.
name|fsDataOStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Exception closing output-stream"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|class|LogReader
specifier|public
specifier|static
class|class
name|LogReader
block|{
DECL|field|fsDataIStream
specifier|private
specifier|final
name|FSDataInputStream
name|fsDataIStream
decl_stmt|;
DECL|field|scanner
specifier|private
specifier|final
name|TFile
operator|.
name|Reader
operator|.
name|Scanner
name|scanner
decl_stmt|;
DECL|field|reader
specifier|private
specifier|final
name|TFile
operator|.
name|Reader
name|reader
decl_stmt|;
DECL|method|LogReader (Configuration conf, Path remoteAppLogFile)
specifier|public
name|LogReader
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|Path
name|remoteAppLogFile
parameter_list|)
throws|throws
name|IOException
block|{
name|FileContext
name|fileContext
init|=
name|FileContext
operator|.
name|getFileContext
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|this
operator|.
name|fsDataIStream
operator|=
name|fileContext
operator|.
name|open
argument_list|(
name|remoteAppLogFile
argument_list|)
expr_stmt|;
name|reader
operator|=
operator|new
name|TFile
operator|.
name|Reader
argument_list|(
name|this
operator|.
name|fsDataIStream
argument_list|,
name|fileContext
operator|.
name|getFileStatus
argument_list|(
name|remoteAppLogFile
argument_list|)
operator|.
name|getLen
argument_list|()
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|this
operator|.
name|scanner
operator|=
name|reader
operator|.
name|createScanner
argument_list|()
expr_stmt|;
block|}
DECL|field|atBeginning
specifier|private
name|boolean
name|atBeginning
init|=
literal|true
decl_stmt|;
comment|/**      * Returns the owner of the application.      *       * @return the application owner.      * @throws IOException      */
DECL|method|getApplicationOwner ()
specifier|public
name|String
name|getApplicationOwner
parameter_list|()
throws|throws
name|IOException
block|{
name|TFile
operator|.
name|Reader
operator|.
name|Scanner
name|ownerScanner
init|=
name|reader
operator|.
name|createScanner
argument_list|()
decl_stmt|;
name|LogKey
name|key
init|=
operator|new
name|LogKey
argument_list|()
decl_stmt|;
while|while
condition|(
operator|!
name|ownerScanner
operator|.
name|atEnd
argument_list|()
condition|)
block|{
name|TFile
operator|.
name|Reader
operator|.
name|Scanner
operator|.
name|Entry
name|entry
init|=
name|ownerScanner
operator|.
name|entry
argument_list|()
decl_stmt|;
name|key
operator|.
name|readFields
argument_list|(
name|entry
operator|.
name|getKeyStream
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|key
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
name|APPLICATION_OWNER_KEY
operator|.
name|toString
argument_list|()
argument_list|)
condition|)
block|{
name|DataInputStream
name|valueStream
init|=
name|entry
operator|.
name|getValueStream
argument_list|()
decl_stmt|;
return|return
name|valueStream
operator|.
name|readUTF
argument_list|()
return|;
block|}
name|ownerScanner
operator|.
name|advance
argument_list|()
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
comment|/**      * Returns ACLs for the application. An empty map is returned if no ACLs are      * found.      *       * @return a map of the Application ACLs.      * @throws IOException      */
DECL|method|getApplicationAcls ()
specifier|public
name|Map
argument_list|<
name|ApplicationAccessType
argument_list|,
name|String
argument_list|>
name|getApplicationAcls
parameter_list|()
throws|throws
name|IOException
block|{
comment|// TODO Seek directly to the key once a comparator is specified.
name|TFile
operator|.
name|Reader
operator|.
name|Scanner
name|aclScanner
init|=
name|reader
operator|.
name|createScanner
argument_list|()
decl_stmt|;
name|LogKey
name|key
init|=
operator|new
name|LogKey
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|ApplicationAccessType
argument_list|,
name|String
argument_list|>
name|acls
init|=
operator|new
name|HashMap
argument_list|<
name|ApplicationAccessType
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
operator|!
name|aclScanner
operator|.
name|atEnd
argument_list|()
condition|)
block|{
name|TFile
operator|.
name|Reader
operator|.
name|Scanner
operator|.
name|Entry
name|entry
init|=
name|aclScanner
operator|.
name|entry
argument_list|()
decl_stmt|;
name|key
operator|.
name|readFields
argument_list|(
name|entry
operator|.
name|getKeyStream
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|key
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
name|APPLICATION_ACL_KEY
operator|.
name|toString
argument_list|()
argument_list|)
condition|)
block|{
name|DataInputStream
name|valueStream
init|=
name|entry
operator|.
name|getValueStream
argument_list|()
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|String
name|appAccessOp
init|=
literal|null
decl_stmt|;
name|String
name|aclString
init|=
literal|null
decl_stmt|;
try|try
block|{
name|appAccessOp
operator|=
name|valueStream
operator|.
name|readUTF
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EOFException
name|e
parameter_list|)
block|{
comment|// Valid end of stream.
break|break;
block|}
try|try
block|{
name|aclString
operator|=
name|valueStream
operator|.
name|readUTF
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EOFException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|YarnException
argument_list|(
literal|"Error reading ACLs"
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|acls
operator|.
name|put
argument_list|(
name|ApplicationAccessType
operator|.
name|valueOf
argument_list|(
name|appAccessOp
argument_list|)
argument_list|,
name|aclString
argument_list|)
expr_stmt|;
block|}
block|}
name|aclScanner
operator|.
name|advance
argument_list|()
expr_stmt|;
block|}
return|return
name|acls
return|;
block|}
comment|/**      * Read the next key and return the value-stream.      *       * @param key      * @return the valueStream if there are more keys or null otherwise.      * @throws IOException      */
DECL|method|next (LogKey key)
specifier|public
name|DataInputStream
name|next
parameter_list|(
name|LogKey
name|key
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|this
operator|.
name|atBeginning
condition|)
block|{
name|this
operator|.
name|scanner
operator|.
name|advance
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|atBeginning
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|scanner
operator|.
name|atEnd
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|TFile
operator|.
name|Reader
operator|.
name|Scanner
operator|.
name|Entry
name|entry
init|=
name|this
operator|.
name|scanner
operator|.
name|entry
argument_list|()
decl_stmt|;
name|key
operator|.
name|readFields
argument_list|(
name|entry
operator|.
name|getKeyStream
argument_list|()
argument_list|)
expr_stmt|;
comment|// Skip META keys
if|if
condition|(
name|RESERVED_KEYS
operator|.
name|containsKey
argument_list|(
name|key
operator|.
name|toString
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|next
argument_list|(
name|key
argument_list|)
return|;
block|}
name|DataInputStream
name|valueStream
init|=
name|entry
operator|.
name|getValueStream
argument_list|()
decl_stmt|;
return|return
name|valueStream
return|;
block|}
comment|//TODO  Change Log format and interfaces to be containerId specific.
comment|// Avoid returning completeValueStreams.
comment|//    public List<String> getTypesForContainer(DataInputStream valueStream){}
comment|//
comment|//    /**
comment|//     * @param valueStream
comment|//     *          The Log stream for the container.
comment|//     * @param fileType
comment|//     *          the log type required.
comment|//     * @return An InputStreamReader for the required log type or null if the
comment|//     *         type is not found.
comment|//     * @throws IOException
comment|//     */
comment|//    public InputStreamReader getLogStreamForType(DataInputStream valueStream,
comment|//        String fileType) throws IOException {
comment|//      valueStream.reset();
comment|//      try {
comment|//        while (true) {
comment|//          String ft = valueStream.readUTF();
comment|//          String fileLengthStr = valueStream.readUTF();
comment|//          long fileLength = Long.parseLong(fileLengthStr);
comment|//          if (ft.equals(fileType)) {
comment|//            BoundedInputStream bis =
comment|//                new BoundedInputStream(valueStream, fileLength);
comment|//            return new InputStreamReader(bis);
comment|//          } else {
comment|//            long totalSkipped = 0;
comment|//            long currSkipped = 0;
comment|//            while (currSkipped != -1&& totalSkipped< fileLength) {
comment|//              currSkipped = valueStream.skip(fileLength - totalSkipped);
comment|//              totalSkipped += currSkipped;
comment|//            }
comment|//            // TODO Verify skip behaviour.
comment|//            if (currSkipped == -1) {
comment|//              return null;
comment|//            }
comment|//          }
comment|//        }
comment|//      } catch (EOFException e) {
comment|//        return null;
comment|//      }
comment|//    }
comment|/**      * Writes all logs for a single container to the provided writer.      * @param valueStream      * @param writer      * @throws IOException      */
DECL|method|readAcontainerLogs (DataInputStream valueStream, Writer writer)
specifier|public
specifier|static
name|void
name|readAcontainerLogs
parameter_list|(
name|DataInputStream
name|valueStream
parameter_list|,
name|Writer
name|writer
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|bufferSize
init|=
literal|65536
decl_stmt|;
name|char
index|[]
name|cbuf
init|=
operator|new
name|char
index|[
name|bufferSize
index|]
decl_stmt|;
name|String
name|fileType
decl_stmt|;
name|String
name|fileLengthStr
decl_stmt|;
name|long
name|fileLength
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
try|try
block|{
name|fileType
operator|=
name|valueStream
operator|.
name|readUTF
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EOFException
name|e
parameter_list|)
block|{
comment|// EndOfFile
return|return;
block|}
name|fileLengthStr
operator|=
name|valueStream
operator|.
name|readUTF
argument_list|()
expr_stmt|;
name|fileLength
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|fileLengthStr
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|"\n\nLogType:"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|fileType
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|"\nLogLength:"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|fileLengthStr
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|"\nLog Contents:\n"
argument_list|)
expr_stmt|;
comment|// ByteLevel
name|BoundedInputStream
name|bis
init|=
operator|new
name|BoundedInputStream
argument_list|(
name|valueStream
argument_list|,
name|fileLength
argument_list|)
decl_stmt|;
name|InputStreamReader
name|reader
init|=
operator|new
name|InputStreamReader
argument_list|(
name|bis
argument_list|)
decl_stmt|;
name|int
name|currentRead
init|=
literal|0
decl_stmt|;
name|int
name|totalRead
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|(
name|currentRead
operator|=
name|reader
operator|.
name|read
argument_list|(
name|cbuf
argument_list|,
literal|0
argument_list|,
name|bufferSize
argument_list|)
operator|)
operator|!=
operator|-
literal|1
condition|)
block|{
name|writer
operator|.
name|write
argument_list|(
name|cbuf
argument_list|)
expr_stmt|;
name|totalRead
operator|+=
name|currentRead
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Keep calling this till you get a {@link EOFException} for getting logs of      * all types for a single container.      *       * @param valueStream      * @param out      * @throws IOException      */
DECL|method|readAContainerLogsForALogType ( DataInputStream valueStream, DataOutputStream out)
specifier|public
specifier|static
name|void
name|readAContainerLogsForALogType
parameter_list|(
name|DataInputStream
name|valueStream
parameter_list|,
name|DataOutputStream
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
literal|65535
index|]
decl_stmt|;
name|String
name|fileType
init|=
name|valueStream
operator|.
name|readUTF
argument_list|()
decl_stmt|;
name|String
name|fileLengthStr
init|=
name|valueStream
operator|.
name|readUTF
argument_list|()
decl_stmt|;
name|long
name|fileLength
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|fileLengthStr
argument_list|)
decl_stmt|;
name|out
operator|.
name|writeUTF
argument_list|(
literal|"\nLogType:"
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeUTF
argument_list|(
name|fileType
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeUTF
argument_list|(
literal|"\nLogLength:"
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeUTF
argument_list|(
name|fileLengthStr
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeUTF
argument_list|(
literal|"\nLog Contents:\n"
argument_list|)
expr_stmt|;
name|int
name|curRead
init|=
literal|0
decl_stmt|;
name|long
name|pendingRead
init|=
name|fileLength
operator|-
name|curRead
decl_stmt|;
name|int
name|toRead
init|=
name|pendingRead
operator|>
name|buf
operator|.
name|length
condition|?
name|buf
operator|.
name|length
else|:
operator|(
name|int
operator|)
name|pendingRead
decl_stmt|;
name|int
name|len
init|=
name|valueStream
operator|.
name|read
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|toRead
argument_list|)
decl_stmt|;
while|while
condition|(
name|len
operator|!=
operator|-
literal|1
operator|&&
name|curRead
operator|<
name|fileLength
condition|)
block|{
name|out
operator|.
name|write
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|curRead
operator|+=
name|len
expr_stmt|;
name|pendingRead
operator|=
name|fileLength
operator|-
name|curRead
expr_stmt|;
name|toRead
operator|=
name|pendingRead
operator|>
name|buf
operator|.
name|length
condition|?
name|buf
operator|.
name|length
else|:
operator|(
name|int
operator|)
name|pendingRead
expr_stmt|;
name|len
operator|=
name|valueStream
operator|.
name|read
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|toRead
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|this
operator|.
name|scanner
operator|.
name|close
argument_list|()
expr_stmt|;
name|this
operator|.
name|fsDataIStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

