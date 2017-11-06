begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.service
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|service
package|;
end_package

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
name|FileUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|curator
operator|.
name|test
operator|.
name|TestingCluster
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
name|hdfs
operator|.
name|HdfsConfiguration
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
name|MiniDFSCluster
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
name|service
operator|.
name|api
operator|.
name|records
operator|.
name|Service
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
name|service
operator|.
name|conf
operator|.
name|YarnServiceConf
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
name|server
operator|.
name|MiniYARNCluster
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
name|service
operator|.
name|api
operator|.
name|records
operator|.
name|Component
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
name|service
operator|.
name|api
operator|.
name|records
operator|.
name|Resource
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
name|service
operator|.
name|utils
operator|.
name|JsonSerDeser
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
name|service
operator|.
name|utils
operator|.
name|ServiceApiUtil
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
name|service
operator|.
name|utils
operator|.
name|SliderFileSystem
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
name|LinuxResourceCalculatorPlugin
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
name|ProcfsBasedProcessTree
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jackson
operator|.
name|map
operator|.
name|PropertyNamingStrategy
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
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
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
name|FileOutputStream
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
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
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
name|registry
operator|.
name|client
operator|.
name|api
operator|.
name|RegistryConstants
operator|.
name|KEY_REGISTRY_ZK_QUORUM
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
name|yarn
operator|.
name|conf
operator|.
name|YarnConfiguration
operator|.
name|DEBUG_NM_DELETE_DELAY_SEC
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
name|yarn
operator|.
name|conf
operator|.
name|YarnConfiguration
operator|.
name|NM_PMEM_CHECK_ENABLED
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
name|yarn
operator|.
name|conf
operator|.
name|YarnConfiguration
operator|.
name|NM_VMEM_CHECK_ENABLED
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
name|yarn
operator|.
name|conf
operator|.
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_ENABLED
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
name|yarn
operator|.
name|service
operator|.
name|conf
operator|.
name|YarnServiceConf
operator|.
name|AM_RESOURCE_MEM
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
name|yarn
operator|.
name|service
operator|.
name|conf
operator|.
name|YarnServiceConf
operator|.
name|YARN_SERVICE_BASE_PATH
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|anyObject
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

begin_class
DECL|class|ServiceTestUtils
specifier|public
class|class
name|ServiceTestUtils
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
name|ServiceTestUtils
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|yarnCluster
specifier|private
name|MiniYARNCluster
name|yarnCluster
init|=
literal|null
decl_stmt|;
DECL|field|hdfsCluster
specifier|private
name|MiniDFSCluster
name|hdfsCluster
init|=
literal|null
decl_stmt|;
DECL|field|fs
specifier|private
name|FileSystem
name|fs
init|=
literal|null
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
init|=
literal|null
decl_stmt|;
DECL|field|NUM_NMS
specifier|public
specifier|static
specifier|final
name|int
name|NUM_NMS
init|=
literal|1
decl_stmt|;
DECL|field|basedir
specifier|private
name|File
name|basedir
decl_stmt|;
DECL|field|JSON_SER_DESER
specifier|public
specifier|static
specifier|final
name|JsonSerDeser
argument_list|<
name|Service
argument_list|>
name|JSON_SER_DESER
init|=
operator|new
name|JsonSerDeser
argument_list|<>
argument_list|(
name|Service
operator|.
name|class
argument_list|,
name|PropertyNamingStrategy
operator|.
name|CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES
argument_list|)
decl_stmt|;
comment|// Example service definition
comment|// 2 components, each of which has 2 containers.
DECL|method|createExampleApplication ()
specifier|protected
name|Service
name|createExampleApplication
parameter_list|()
block|{
name|Service
name|exampleApp
init|=
operator|new
name|Service
argument_list|()
decl_stmt|;
name|exampleApp
operator|.
name|setName
argument_list|(
literal|"example-app"
argument_list|)
expr_stmt|;
name|exampleApp
operator|.
name|addComponent
argument_list|(
name|createComponent
argument_list|(
literal|"compa"
argument_list|)
argument_list|)
expr_stmt|;
name|exampleApp
operator|.
name|addComponent
argument_list|(
name|createComponent
argument_list|(
literal|"compb"
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|exampleApp
return|;
block|}
DECL|method|createComponent (String name)
specifier|public
specifier|static
name|Component
name|createComponent
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|createComponent
argument_list|(
name|name
argument_list|,
literal|2L
argument_list|,
literal|"sleep 1000"
argument_list|)
return|;
block|}
DECL|method|createComponent (String name, long numContainers, String command)
specifier|protected
specifier|static
name|Component
name|createComponent
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|numContainers
parameter_list|,
name|String
name|command
parameter_list|)
block|{
name|Component
name|comp1
init|=
operator|new
name|Component
argument_list|()
decl_stmt|;
name|comp1
operator|.
name|setNumberOfContainers
argument_list|(
name|numContainers
argument_list|)
expr_stmt|;
name|comp1
operator|.
name|setLaunchCommand
argument_list|(
name|command
argument_list|)
expr_stmt|;
name|comp1
operator|.
name|setName
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|Resource
name|resource
init|=
operator|new
name|Resource
argument_list|()
decl_stmt|;
name|comp1
operator|.
name|setResource
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|resource
operator|.
name|setMemory
argument_list|(
literal|"128"
argument_list|)
expr_stmt|;
name|resource
operator|.
name|setCpus
argument_list|(
literal|1
argument_list|)
expr_stmt|;
return|return
name|comp1
return|;
block|}
DECL|method|initMockFs ()
specifier|public
specifier|static
name|SliderFileSystem
name|initMockFs
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|initMockFs
argument_list|(
literal|null
argument_list|)
return|;
block|}
DECL|method|initMockFs (Service ext)
specifier|public
specifier|static
name|SliderFileSystem
name|initMockFs
parameter_list|(
name|Service
name|ext
parameter_list|)
throws|throws
name|IOException
block|{
name|SliderFileSystem
name|sfs
init|=
name|mock
argument_list|(
name|SliderFileSystem
operator|.
name|class
argument_list|)
decl_stmt|;
name|FileSystem
name|mockFs
init|=
name|mock
argument_list|(
name|FileSystem
operator|.
name|class
argument_list|)
decl_stmt|;
name|JsonSerDeser
argument_list|<
name|Service
argument_list|>
name|jsonSerDeser
init|=
name|mock
argument_list|(
name|JsonSerDeser
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|sfs
operator|.
name|getFileSystem
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|mockFs
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|sfs
operator|.
name|buildClusterDirPath
argument_list|(
name|anyObject
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|Path
argument_list|(
literal|"cluster_dir_path"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|ext
operator|!=
literal|null
condition|)
block|{
name|when
argument_list|(
name|jsonSerDeser
operator|.
name|load
argument_list|(
name|anyObject
argument_list|()
argument_list|,
name|anyObject
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|ext
argument_list|)
expr_stmt|;
block|}
name|ServiceApiUtil
operator|.
name|setJsonSerDeser
argument_list|(
name|jsonSerDeser
argument_list|)
expr_stmt|;
return|return
name|sfs
return|;
block|}
DECL|method|setConf (YarnConfiguration conf)
specifier|protected
name|void
name|setConf
parameter_list|(
name|YarnConfiguration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
block|}
DECL|method|getConf ()
specifier|protected
name|Configuration
name|getConf
parameter_list|()
block|{
return|return
name|conf
return|;
block|}
DECL|method|getFS ()
specifier|protected
name|FileSystem
name|getFS
parameter_list|()
block|{
return|return
name|fs
return|;
block|}
DECL|method|getYarnCluster ()
specifier|protected
name|MiniYARNCluster
name|getYarnCluster
parameter_list|()
block|{
return|return
name|yarnCluster
return|;
block|}
DECL|method|setupInternal (int numNodeManager)
specifier|protected
name|void
name|setupInternal
parameter_list|(
name|int
name|numNodeManager
parameter_list|)
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting up YARN cluster"
argument_list|)
expr_stmt|;
if|if
condition|(
name|conf
operator|==
literal|null
condition|)
block|{
name|setConf
argument_list|(
operator|new
name|YarnConfiguration
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|conf
operator|.
name|setInt
argument_list|(
name|YarnConfiguration
operator|.
name|RM_SCHEDULER_MINIMUM_ALLOCATION_MB
argument_list|,
literal|128
argument_list|)
expr_stmt|;
comment|// reduce the teardown waiting time
name|conf
operator|.
name|setLong
argument_list|(
name|YarnConfiguration
operator|.
name|DISPATCHER_DRAIN_EVENTS_TIMEOUT
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"yarn.log.dir"
argument_list|,
literal|"target"
argument_list|)
expr_stmt|;
comment|// mark if we need to launch the v1 timeline server
comment|// disable aux-service based timeline aggregators
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_AUX_SERVICES
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_VMEM_PMEM_RATIO
argument_list|,
literal|"8"
argument_list|)
expr_stmt|;
comment|// Enable ContainersMonitorImpl
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_CONTAINER_MON_RESOURCE_CALCULATOR
argument_list|,
name|LinuxResourceCalculatorPlugin
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_CONTAINER_MON_PROCESS_TREE
argument_list|,
name|ProcfsBasedProcessTree
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|YARN_MINICLUSTER_CONTROL_RESOURCE_MONITORING
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|TIMELINE_SERVICE_ENABLED
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|YarnConfiguration
operator|.
name|NM_MAX_PER_DISK_UTILIZATION_PERCENTAGE
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|DEBUG_NM_DELETE_DELAY_SEC
argument_list|,
literal|60000
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|AM_RESOURCE_MEM
argument_list|,
literal|526
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|YarnServiceConf
operator|.
name|READINESS_CHECK_INTERVAL
argument_list|,
literal|5
argument_list|)
expr_stmt|;
comment|// Disable vmem check to disallow NM killing the container
name|conf
operator|.
name|setBoolean
argument_list|(
name|NM_VMEM_CHECK_ENABLED
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|NM_PMEM_CHECK_ENABLED
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// setup zk cluster
name|TestingCluster
name|zkCluster
decl_stmt|;
name|zkCluster
operator|=
operator|new
name|TestingCluster
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|zkCluster
operator|.
name|start
argument_list|()
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_ZK_ADDRESS
argument_list|,
name|zkCluster
operator|.
name|getConnectString
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|KEY_REGISTRY_ZK_QUORUM
argument_list|,
name|zkCluster
operator|.
name|getConnectString
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"ZK cluster: "
operator|+
name|zkCluster
operator|.
name|getConnectString
argument_list|()
argument_list|)
expr_stmt|;
name|fs
operator|=
name|FileSystem
operator|.
name|get
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|basedir
operator|=
operator|new
name|File
argument_list|(
literal|"target"
argument_list|,
literal|"apps"
argument_list|)
expr_stmt|;
if|if
condition|(
name|basedir
operator|.
name|exists
argument_list|()
condition|)
block|{
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|basedir
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|basedir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
block|}
name|conf
operator|.
name|set
argument_list|(
name|YARN_SERVICE_BASE_PATH
argument_list|,
name|basedir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|yarnCluster
operator|==
literal|null
condition|)
block|{
name|yarnCluster
operator|=
operator|new
name|MiniYARNCluster
argument_list|(
name|TestYarnNativeServices
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|,
literal|1
argument_list|,
name|numNodeManager
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|yarnCluster
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|yarnCluster
operator|.
name|start
argument_list|()
expr_stmt|;
name|waitForNMsToRegister
argument_list|()
expr_stmt|;
name|URL
name|url
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getContextClassLoader
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"yarn-site.xml"
argument_list|)
decl_stmt|;
if|if
condition|(
name|url
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Could not find 'yarn-site.xml' dummy file in classpath"
argument_list|)
throw|;
block|}
name|Configuration
name|yarnClusterConfig
init|=
name|yarnCluster
operator|.
name|getConfig
argument_list|()
decl_stmt|;
name|yarnClusterConfig
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|YARN_APPLICATION_CLASSPATH
argument_list|,
operator|new
name|File
argument_list|(
name|url
operator|.
name|getPath
argument_list|()
argument_list|)
operator|.
name|getParent
argument_list|()
argument_list|)
expr_stmt|;
comment|//write the document to a buffer (not directly to the file, as that
comment|//can cause the file being written to get read -which will then fail.
name|ByteArrayOutputStream
name|bytesOut
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|yarnClusterConfig
operator|.
name|writeXml
argument_list|(
name|bytesOut
argument_list|)
expr_stmt|;
name|bytesOut
operator|.
name|close
argument_list|()
expr_stmt|;
comment|//write the bytes to the file in the classpath
name|OutputStream
name|os
init|=
operator|new
name|FileOutputStream
argument_list|(
operator|new
name|File
argument_list|(
name|url
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|os
operator|.
name|write
argument_list|(
name|bytesOut
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Write yarn-site.xml configs to: "
operator|+
name|url
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|hdfsCluster
operator|==
literal|null
condition|)
block|{
name|HdfsConfiguration
name|hdfsConfig
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|hdfsCluster
operator|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|hdfsConfig
argument_list|)
operator|.
name|numDataNodes
argument_list|(
literal|1
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"setup thread sleep interrupted. message="
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|shutdown ()
specifier|public
name|void
name|shutdown
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|yarnCluster
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|yarnCluster
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|yarnCluster
operator|=
literal|null
expr_stmt|;
block|}
block|}
if|if
condition|(
name|hdfsCluster
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|hdfsCluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|hdfsCluster
operator|=
literal|null
expr_stmt|;
block|}
block|}
if|if
condition|(
name|basedir
operator|!=
literal|null
condition|)
block|{
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|basedir
argument_list|)
expr_stmt|;
block|}
name|SliderFileSystem
name|sfs
init|=
operator|new
name|SliderFileSystem
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|Path
name|appDir
init|=
name|sfs
operator|.
name|getBaseApplicationPath
argument_list|()
decl_stmt|;
name|sfs
operator|.
name|getFileSystem
argument_list|()
operator|.
name|delete
argument_list|(
name|appDir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|waitForNMsToRegister ()
specifier|private
name|void
name|waitForNMsToRegister
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|sec
init|=
literal|60
decl_stmt|;
while|while
condition|(
name|sec
operator|>=
literal|0
condition|)
block|{
if|if
condition|(
name|yarnCluster
operator|.
name|getResourceManager
argument_list|()
operator|.
name|getRMContext
argument_list|()
operator|.
name|getRMNodes
argument_list|()
operator|.
name|size
argument_list|()
operator|>=
name|NUM_NMS
condition|)
block|{
break|break;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|sec
operator|--
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

