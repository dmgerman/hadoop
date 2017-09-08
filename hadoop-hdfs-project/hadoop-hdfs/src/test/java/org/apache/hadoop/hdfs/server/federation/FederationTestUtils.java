begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.federation
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|federation
package|;
end_package

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
name|assertNull
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
name|fail
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
import|;
end_import

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
name|reflect
operator|.
name|InvocationTargetException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
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
name|List
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
name|management
operator|.
name|JMX
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|MBeanServer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|MalformedObjectNameException
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
name|UnsupportedFileSystemException
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
name|ha
operator|.
name|HAServiceProtocol
operator|.
name|HAServiceState
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
name|hdfs
operator|.
name|DFSConfigKeys
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
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|resolver
operator|.
name|ActiveNamenodeResolver
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
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|resolver
operator|.
name|FederationNamenodeContext
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
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|resolver
operator|.
name|FederationNamenodeServiceState
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
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|resolver
operator|.
name|NamenodeStatusReport
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
name|hdfs
operator|.
name|server
operator|.
name|protocol
operator|.
name|NamespaceInfo
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
name|AccessControlException
import|;
end_import

begin_comment
comment|/**  * Helper utilities for testing HDFS Federation.  */
end_comment

begin_class
DECL|class|FederationTestUtils
specifier|public
specifier|final
class|class
name|FederationTestUtils
block|{
DECL|field|NAMESERVICES
specifier|public
specifier|final
specifier|static
name|String
index|[]
name|NAMESERVICES
init|=
block|{
literal|"ns0"
block|,
literal|"ns1"
block|}
decl_stmt|;
DECL|field|NAMENODES
specifier|public
specifier|final
specifier|static
name|String
index|[]
name|NAMENODES
init|=
block|{
literal|"nn0"
block|,
literal|"nn1"
block|,
literal|"nn2"
block|,
literal|"nn3"
block|}
decl_stmt|;
DECL|field|ROUTERS
specifier|public
specifier|final
specifier|static
name|String
index|[]
name|ROUTERS
init|=
block|{
literal|"router0"
block|,
literal|"router1"
block|,
literal|"router2"
block|,
literal|"router3"
block|}
decl_stmt|;
DECL|method|FederationTestUtils ()
specifier|private
name|FederationTestUtils
parameter_list|()
block|{
comment|// Utility class
block|}
DECL|method|verifyException (Object obj, String methodName, Class<? extends Exception> exceptionClass, Class<?>[] parameterTypes, Object[] arguments)
specifier|public
specifier|static
name|void
name|verifyException
parameter_list|(
name|Object
name|obj
parameter_list|,
name|String
name|methodName
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|Exception
argument_list|>
name|exceptionClass
parameter_list|,
name|Class
argument_list|<
name|?
argument_list|>
index|[]
name|parameterTypes
parameter_list|,
name|Object
index|[]
name|arguments
parameter_list|)
block|{
name|Throwable
name|triggeredException
init|=
literal|null
decl_stmt|;
try|try
block|{
name|Method
name|m
init|=
name|obj
operator|.
name|getClass
argument_list|()
operator|.
name|getMethod
argument_list|(
name|methodName
argument_list|,
name|parameterTypes
argument_list|)
decl_stmt|;
name|m
operator|.
name|invoke
argument_list|(
name|obj
argument_list|,
name|arguments
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvocationTargetException
name|ex
parameter_list|)
block|{
name|triggeredException
operator|=
name|ex
operator|.
name|getTargetException
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|triggeredException
operator|=
name|e
expr_stmt|;
block|}
if|if
condition|(
name|exceptionClass
operator|!=
literal|null
condition|)
block|{
name|assertNotNull
argument_list|(
literal|"No exception was triggered, expected exception"
operator|+
name|exceptionClass
operator|.
name|getName
argument_list|()
argument_list|,
name|triggeredException
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|exceptionClass
argument_list|,
name|triggeredException
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertNull
argument_list|(
literal|"Exception was triggered but no exception was expected"
argument_list|,
name|triggeredException
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|createNamenodeReport (String ns, String nn, HAServiceState state)
specifier|public
specifier|static
name|NamenodeStatusReport
name|createNamenodeReport
parameter_list|(
name|String
name|ns
parameter_list|,
name|String
name|nn
parameter_list|,
name|HAServiceState
name|state
parameter_list|)
block|{
name|Random
name|rand
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
name|NamenodeStatusReport
name|report
init|=
operator|new
name|NamenodeStatusReport
argument_list|(
name|ns
argument_list|,
name|nn
argument_list|,
literal|"localhost:"
operator|+
name|rand
operator|.
name|nextInt
argument_list|(
literal|10000
argument_list|)
argument_list|,
literal|"localhost:"
operator|+
name|rand
operator|.
name|nextInt
argument_list|(
literal|10000
argument_list|)
argument_list|,
literal|"localhost:"
operator|+
name|rand
operator|.
name|nextInt
argument_list|(
literal|10000
argument_list|)
argument_list|,
literal|"testwebaddress-"
operator|+
name|ns
operator|+
name|nn
argument_list|)
decl_stmt|;
if|if
condition|(
name|state
operator|==
literal|null
condition|)
block|{
comment|// Unavailable, no additional info
return|return
name|report
return|;
block|}
name|report
operator|.
name|setHAServiceState
argument_list|(
name|state
argument_list|)
expr_stmt|;
name|NamespaceInfo
name|nsInfo
init|=
operator|new
name|NamespaceInfo
argument_list|(
literal|1
argument_list|,
literal|"tesclusterid"
argument_list|,
name|ns
argument_list|,
literal|0
argument_list|,
literal|"testbuildvesion"
argument_list|,
literal|"testsoftwareversion"
argument_list|)
decl_stmt|;
name|report
operator|.
name|setNamespaceInfo
argument_list|(
name|nsInfo
argument_list|)
expr_stmt|;
return|return
name|report
return|;
block|}
DECL|method|waitNamenodeRegistered (ActiveNamenodeResolver resolver, String nsId, String nnId, FederationNamenodeServiceState finalState)
specifier|public
specifier|static
name|void
name|waitNamenodeRegistered
parameter_list|(
name|ActiveNamenodeResolver
name|resolver
parameter_list|,
name|String
name|nsId
parameter_list|,
name|String
name|nnId
parameter_list|,
name|FederationNamenodeServiceState
name|finalState
parameter_list|)
throws|throws
name|InterruptedException
throws|,
name|IllegalStateException
throws|,
name|IOException
block|{
for|for
control|(
name|int
name|loopCount
init|=
literal|0
init|;
name|loopCount
operator|<
literal|20
condition|;
name|loopCount
operator|++
control|)
block|{
if|if
condition|(
name|loopCount
operator|>
literal|0
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|?
extends|extends
name|FederationNamenodeContext
argument_list|>
name|namenodes
init|=
name|resolver
operator|.
name|getNamenodesForNameserviceId
argument_list|(
name|nsId
argument_list|)
decl_stmt|;
for|for
control|(
name|FederationNamenodeContext
name|namenode
range|:
name|namenodes
control|)
block|{
comment|// Check if this is the Namenode we are checking
if|if
condition|(
name|namenode
operator|.
name|getNamenodeId
argument_list|()
operator|==
name|nnId
operator|||
name|namenode
operator|.
name|getNamenodeId
argument_list|()
operator|.
name|equals
argument_list|(
name|nnId
argument_list|)
condition|)
block|{
if|if
condition|(
name|finalState
operator|!=
literal|null
operator|&&
operator|!
name|namenode
operator|.
name|getState
argument_list|()
operator|.
name|equals
argument_list|(
name|finalState
argument_list|)
condition|)
block|{
comment|// Wrong state, wait a bit more
break|break;
block|}
else|else
block|{
comment|// Found and verified
return|return;
block|}
block|}
block|}
block|}
name|fail
argument_list|(
literal|"Failed to verify State Store registration of "
operator|+
name|nsId
operator|+
literal|" "
operator|+
name|nnId
operator|+
literal|" for state "
operator|+
name|finalState
argument_list|)
expr_stmt|;
block|}
DECL|method|verifyDate (Date d1, Date d2, long precision)
specifier|public
specifier|static
name|boolean
name|verifyDate
parameter_list|(
name|Date
name|d1
parameter_list|,
name|Date
name|d2
parameter_list|,
name|long
name|precision
parameter_list|)
block|{
return|return
name|Math
operator|.
name|abs
argument_list|(
name|d1
operator|.
name|getTime
argument_list|()
operator|-
name|d2
operator|.
name|getTime
argument_list|()
argument_list|)
operator|<
name|precision
return|;
block|}
DECL|method|getBean (String name, Class<T> obj)
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|T
name|getBean
parameter_list|(
name|String
name|name
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|obj
parameter_list|)
throws|throws
name|MalformedObjectNameException
block|{
name|MBeanServer
name|mBeanServer
init|=
name|ManagementFactory
operator|.
name|getPlatformMBeanServer
argument_list|()
decl_stmt|;
name|ObjectName
name|poolName
init|=
operator|new
name|ObjectName
argument_list|(
name|name
argument_list|)
decl_stmt|;
return|return
name|JMX
operator|.
name|newMXBeanProxy
argument_list|(
name|mBeanServer
argument_list|,
name|poolName
argument_list|,
name|obj
argument_list|)
return|;
block|}
DECL|method|addDirectory (FileSystem context, String path)
specifier|public
specifier|static
name|boolean
name|addDirectory
parameter_list|(
name|FileSystem
name|context
parameter_list|,
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|context
operator|.
name|mkdirs
argument_list|(
operator|new
name|Path
argument_list|(
name|path
argument_list|)
argument_list|,
operator|new
name|FsPermission
argument_list|(
literal|"777"
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|verifyFileExists
argument_list|(
name|context
argument_list|,
name|path
argument_list|)
return|;
block|}
DECL|method|getFileStatus (FileSystem context, String path)
specifier|public
specifier|static
name|FileStatus
name|getFileStatus
parameter_list|(
name|FileSystem
name|context
parameter_list|,
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|context
operator|.
name|getFileStatus
argument_list|(
operator|new
name|Path
argument_list|(
name|path
argument_list|)
argument_list|)
return|;
block|}
DECL|method|verifyFileExists (FileSystem context, String path)
specifier|public
specifier|static
name|boolean
name|verifyFileExists
parameter_list|(
name|FileSystem
name|context
parameter_list|,
name|String
name|path
parameter_list|)
block|{
try|try
block|{
name|FileStatus
name|status
init|=
name|getFileStatus
argument_list|(
name|context
argument_list|,
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|status
operator|!=
literal|null
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|false
return|;
block|}
DECL|method|checkForFileInDirectory ( FileSystem context, String testPath, String targetFile)
specifier|public
specifier|static
name|boolean
name|checkForFileInDirectory
parameter_list|(
name|FileSystem
name|context
parameter_list|,
name|String
name|testPath
parameter_list|,
name|String
name|targetFile
parameter_list|)
throws|throws
name|IOException
throws|,
name|AccessControlException
throws|,
name|FileNotFoundException
throws|,
name|UnsupportedFileSystemException
throws|,
name|IllegalArgumentException
block|{
name|FileStatus
index|[]
name|fileStatus
init|=
name|context
operator|.
name|listStatus
argument_list|(
operator|new
name|Path
argument_list|(
name|testPath
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|file
init|=
literal|null
decl_stmt|;
name|String
name|verifyPath
init|=
name|testPath
operator|+
literal|"/"
operator|+
name|targetFile
decl_stmt|;
if|if
condition|(
name|testPath
operator|.
name|equals
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|verifyPath
operator|=
name|testPath
operator|+
name|targetFile
expr_stmt|;
block|}
name|Boolean
name|found
init|=
literal|false
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
name|fileStatus
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|FileStatus
name|f
init|=
name|fileStatus
index|[
name|i
index|]
decl_stmt|;
name|file
operator|=
name|Path
operator|.
name|getPathWithoutSchemeAndAuthority
argument_list|(
name|f
operator|.
name|getPath
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
if|if
condition|(
name|file
operator|.
name|equals
argument_list|(
name|verifyPath
argument_list|)
condition|)
block|{
name|found
operator|=
literal|true
expr_stmt|;
block|}
block|}
return|return
name|found
return|;
block|}
DECL|method|countContents (FileSystem context, String testPath)
specifier|public
specifier|static
name|int
name|countContents
parameter_list|(
name|FileSystem
name|context
parameter_list|,
name|String
name|testPath
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
name|testPath
argument_list|)
decl_stmt|;
name|FileStatus
index|[]
name|fileStatus
init|=
name|context
operator|.
name|listStatus
argument_list|(
name|path
argument_list|)
decl_stmt|;
return|return
name|fileStatus
operator|.
name|length
return|;
block|}
DECL|method|createFile (FileSystem fs, String path, long length)
specifier|public
specifier|static
name|void
name|createFile
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|String
name|path
parameter_list|,
name|long
name|length
parameter_list|)
throws|throws
name|IOException
block|{
name|FsPermission
name|permissions
init|=
operator|new
name|FsPermission
argument_list|(
literal|"700"
argument_list|)
decl_stmt|;
name|FSDataOutputStream
name|writeStream
init|=
name|fs
operator|.
name|create
argument_list|(
operator|new
name|Path
argument_list|(
name|path
argument_list|)
argument_list|,
name|permissions
argument_list|,
literal|true
argument_list|,
literal|1000
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_BLOCK_SIZE_DEFAULT
argument_list|,
literal|null
argument_list|)
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
name|length
condition|;
name|i
operator|++
control|)
block|{
name|writeStream
operator|.
name|write
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
name|writeStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|readFile (FileSystem fs, String path)
specifier|public
specifier|static
name|String
name|readFile
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Read the file from the filesystem via the active namenode
name|Path
name|fileName
init|=
operator|new
name|Path
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|InputStreamReader
name|reader
init|=
operator|new
name|InputStreamReader
argument_list|(
name|fs
operator|.
name|open
argument_list|(
name|fileName
argument_list|)
argument_list|)
decl_stmt|;
name|BufferedReader
name|bufferedReader
init|=
operator|new
name|BufferedReader
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|StringBuilder
name|data
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|String
name|line
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|bufferedReader
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|data
operator|.
name|append
argument_list|(
name|line
argument_list|)
expr_stmt|;
block|}
name|bufferedReader
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|data
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|deleteFile (FileSystem fs, String path)
specifier|public
specifier|static
name|boolean
name|deleteFile
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|fs
operator|.
name|delete
argument_list|(
operator|new
name|Path
argument_list|(
name|path
argument_list|)
argument_list|,
literal|true
argument_list|)
return|;
block|}
block|}
end_class

end_unit

