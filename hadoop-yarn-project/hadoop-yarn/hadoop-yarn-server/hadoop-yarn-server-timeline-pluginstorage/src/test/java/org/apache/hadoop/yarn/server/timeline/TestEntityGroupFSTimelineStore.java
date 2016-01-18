begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timeline
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
name|timeline
package|;
end_package

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
name|api
operator|.
name|records
operator|.
name|ApplicationAttemptId
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
name|ApplicationId
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
name|timeline
operator|.
name|TimelineEntities
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
name|timeline
operator|.
name|TimelineEntity
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

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
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
name|junit
operator|.
name|rules
operator|.
name|TestName
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
name|List
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
name|server
operator|.
name|timeline
operator|.
name|EntityGroupFSTimelineStore
operator|.
name|AppState
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
DECL|class|TestEntityGroupFSTimelineStore
specifier|public
class|class
name|TestEntityGroupFSTimelineStore
extends|extends
name|TimelineStoreTestUtils
block|{
DECL|field|SAMPLE_APP_NAME
specifier|private
specifier|static
specifier|final
name|String
name|SAMPLE_APP_NAME
init|=
literal|"1234_5678"
decl_stmt|;
DECL|field|TEST_APPLICATION_ID
specifier|static
specifier|final
name|ApplicationId
name|TEST_APPLICATION_ID
init|=
name|ConverterUtils
operator|.
name|toApplicationId
argument_list|(
name|ConverterUtils
operator|.
name|APPLICATION_PREFIX
operator|+
literal|"_"
operator|+
name|SAMPLE_APP_NAME
argument_list|)
decl_stmt|;
DECL|field|TEST_APP_DIR_NAME
specifier|private
specifier|static
specifier|final
name|String
name|TEST_APP_DIR_NAME
init|=
name|TEST_APPLICATION_ID
operator|.
name|toString
argument_list|()
decl_stmt|;
DECL|field|TEST_ATTEMPT_DIR_NAME
specifier|private
specifier|static
specifier|final
name|String
name|TEST_ATTEMPT_DIR_NAME
init|=
name|ApplicationAttemptId
operator|.
name|appAttemptIdStrPrefix
operator|+
name|SAMPLE_APP_NAME
operator|+
literal|"_1"
decl_stmt|;
DECL|field|TEST_SUMMARY_LOG_FILE_NAME
specifier|private
specifier|static
specifier|final
name|String
name|TEST_SUMMARY_LOG_FILE_NAME
init|=
name|EntityGroupFSTimelineStore
operator|.
name|SUMMARY_LOG_PREFIX
operator|+
literal|"test"
decl_stmt|;
DECL|field|TEST_ENTITY_LOG_FILE_NAME
specifier|private
specifier|static
specifier|final
name|String
name|TEST_ENTITY_LOG_FILE_NAME
init|=
name|EntityGroupFSTimelineStore
operator|.
name|ENTITY_LOG_PREFIX
operator|+
name|EntityGroupPlugInForTest
operator|.
name|getStandardTimelineGroupId
argument_list|()
decl_stmt|;
DECL|field|TEST_DOMAIN_LOG_FILE_NAME
specifier|private
specifier|static
specifier|final
name|String
name|TEST_DOMAIN_LOG_FILE_NAME
init|=
name|EntityGroupFSTimelineStore
operator|.
name|DOMAIN_LOG_PREFIX
operator|+
literal|"test"
decl_stmt|;
DECL|field|TEST_ROOT_DIR
specifier|private
specifier|static
specifier|final
name|Path
name|TEST_ROOT_DIR
init|=
operator|new
name|Path
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|,
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.io.tmpdir"
argument_list|)
argument_list|)
argument_list|,
name|TestEntityGroupFSTimelineStore
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|TEST_APP_DIR_PATH
specifier|private
specifier|static
specifier|final
name|Path
name|TEST_APP_DIR_PATH
init|=
operator|new
name|Path
argument_list|(
name|TEST_ROOT_DIR
argument_list|,
name|TEST_APP_DIR_NAME
argument_list|)
decl_stmt|;
DECL|field|TEST_ATTEMPT_DIR_PATH
specifier|private
specifier|static
specifier|final
name|Path
name|TEST_ATTEMPT_DIR_PATH
init|=
operator|new
name|Path
argument_list|(
name|TEST_APP_DIR_PATH
argument_list|,
name|TEST_ATTEMPT_DIR_NAME
argument_list|)
decl_stmt|;
DECL|field|TEST_DONE_DIR_PATH
specifier|private
specifier|static
specifier|final
name|Path
name|TEST_DONE_DIR_PATH
init|=
operator|new
name|Path
argument_list|(
name|TEST_ROOT_DIR
argument_list|,
literal|"done"
argument_list|)
decl_stmt|;
DECL|field|config
specifier|private
specifier|static
name|Configuration
name|config
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
DECL|field|hdfsCluster
specifier|private
specifier|static
name|MiniDFSCluster
name|hdfsCluster
decl_stmt|;
DECL|field|fs
specifier|private
specifier|static
name|FileSystem
name|fs
decl_stmt|;
DECL|field|store
specifier|private
name|EntityGroupFSTimelineStore
name|store
decl_stmt|;
DECL|field|entityNew
specifier|private
name|TimelineEntity
name|entityNew
decl_stmt|;
annotation|@
name|Rule
DECL|field|currTestName
specifier|public
name|TestName
name|currTestName
init|=
operator|new
name|TestName
argument_list|()
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setupClass ()
specifier|public
specifier|static
name|void
name|setupClass
parameter_list|()
throws|throws
name|Exception
block|{
name|config
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_TTL_ENABLE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|config
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_ENTITYGROUP_FS_STORE_SUMMARY_ENTITY_TYPES
argument_list|,
literal|"YARN_APPLICATION,YARN_APPLICATION_ATTEMPT,YARN_CONTAINER"
argument_list|)
expr_stmt|;
name|config
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_ENTITYGROUP_FS_STORE_DONE_DIR
argument_list|,
name|TEST_DONE_DIR_PATH
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|config
operator|.
name|set
argument_list|(
name|MiniDFSCluster
operator|.
name|HDFS_MINIDFS_BASEDIR
argument_list|,
name|TEST_ROOT_DIR
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
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
name|fs
operator|=
name|hdfsCluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|createTestFiles
argument_list|()
expr_stmt|;
name|store
operator|=
operator|new
name|EntityGroupFSTimelineStore
argument_list|()
expr_stmt|;
if|if
condition|(
name|currTestName
operator|.
name|getMethodName
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Plugin"
argument_list|)
condition|)
block|{
name|config
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_ENTITY_GROUP_PLUGIN_CLASSES
argument_list|,
name|EntityGroupPlugInForTest
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|store
operator|.
name|init
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|store
operator|.
name|start
argument_list|()
expr_stmt|;
name|store
operator|.
name|setFs
argument_list|(
name|fs
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|fs
operator|.
name|delete
argument_list|(
name|TEST_APP_DIR_PATH
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|store
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|tearDownClass ()
specifier|public
specifier|static
name|void
name|tearDownClass
parameter_list|()
throws|throws
name|Exception
block|{
name|hdfsCluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|FileContext
name|fileContext
init|=
name|FileContext
operator|.
name|getLocalFSFileContext
argument_list|()
decl_stmt|;
name|fileContext
operator|.
name|delete
argument_list|(
operator|new
name|Path
argument_list|(
name|config
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_LEVELDB_PATH
argument_list|)
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAppLogsScanLogs ()
specifier|public
name|void
name|testAppLogsScanLogs
parameter_list|()
throws|throws
name|Exception
block|{
name|EntityGroupFSTimelineStore
operator|.
name|AppLogs
name|appLogs
init|=
name|store
operator|.
expr|new
name|AppLogs
argument_list|(
name|TEST_APPLICATION_ID
argument_list|,
name|TEST_APP_DIR_PATH
argument_list|,
name|AppState
operator|.
name|COMPLETED
argument_list|)
decl_stmt|;
name|appLogs
operator|.
name|scanForLogs
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|LogInfo
argument_list|>
name|summaryLogs
init|=
name|appLogs
operator|.
name|getSummaryLogs
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|LogInfo
argument_list|>
name|detailLogs
init|=
name|appLogs
operator|.
name|getDetailLogs
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|summaryLogs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|detailLogs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|LogInfo
name|log
range|:
name|summaryLogs
control|)
block|{
name|String
name|fileName
init|=
name|log
operator|.
name|getFilename
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|fileName
operator|.
name|equals
argument_list|(
name|TEST_SUMMARY_LOG_FILE_NAME
argument_list|)
operator|||
name|fileName
operator|.
name|equals
argument_list|(
name|TEST_DOMAIN_LOG_FILE_NAME
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|LogInfo
name|log
range|:
name|detailLogs
control|)
block|{
name|String
name|fileName
init|=
name|log
operator|.
name|getFilename
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|fileName
argument_list|,
name|TEST_ENTITY_LOG_FILE_NAME
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testMoveToDone ()
specifier|public
name|void
name|testMoveToDone
parameter_list|()
throws|throws
name|Exception
block|{
name|EntityGroupFSTimelineStore
operator|.
name|AppLogs
name|appLogs
init|=
name|store
operator|.
expr|new
name|AppLogs
argument_list|(
name|TEST_APPLICATION_ID
argument_list|,
name|TEST_APP_DIR_PATH
argument_list|,
name|AppState
operator|.
name|COMPLETED
argument_list|)
decl_stmt|;
name|Path
name|pathBefore
init|=
name|appLogs
operator|.
name|getAppDirPath
argument_list|()
decl_stmt|;
name|appLogs
operator|.
name|moveToDone
argument_list|()
expr_stmt|;
name|Path
name|pathAfter
init|=
name|appLogs
operator|.
name|getAppDirPath
argument_list|()
decl_stmt|;
name|assertNotEquals
argument_list|(
name|pathBefore
argument_list|,
name|pathAfter
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pathAfter
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
name|TEST_DONE_DIR_PATH
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testParseSummaryLogs ()
specifier|public
name|void
name|testParseSummaryLogs
parameter_list|()
throws|throws
name|Exception
block|{
name|TimelineDataManager
name|tdm
init|=
name|PluginStoreTestUtils
operator|.
name|getTdmWithMemStore
argument_list|(
name|config
argument_list|)
decl_stmt|;
name|EntityGroupFSTimelineStore
operator|.
name|AppLogs
name|appLogs
init|=
name|store
operator|.
expr|new
name|AppLogs
argument_list|(
name|TEST_APPLICATION_ID
argument_list|,
name|TEST_APP_DIR_PATH
argument_list|,
name|AppState
operator|.
name|COMPLETED
argument_list|)
decl_stmt|;
name|appLogs
operator|.
name|scanForLogs
argument_list|()
expr_stmt|;
name|appLogs
operator|.
name|parseSummaryLogs
argument_list|(
name|tdm
argument_list|)
expr_stmt|;
name|PluginStoreTestUtils
operator|.
name|verifyTestEntities
argument_list|(
name|tdm
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCleanLogs ()
specifier|public
name|void
name|testCleanLogs
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Create test dirs and files
comment|// Irrelevant file, should not be reclaimed
name|Path
name|irrelevantFilePath
init|=
operator|new
name|Path
argument_list|(
name|TEST_DONE_DIR_PATH
argument_list|,
literal|"irrelevant.log"
argument_list|)
decl_stmt|;
name|FSDataOutputStream
name|stream
init|=
name|fs
operator|.
name|create
argument_list|(
name|irrelevantFilePath
argument_list|)
decl_stmt|;
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Irrelevant directory, should not be reclaimed
name|Path
name|irrelevantDirPath
init|=
operator|new
name|Path
argument_list|(
name|TEST_DONE_DIR_PATH
argument_list|,
literal|"irrelevant"
argument_list|)
decl_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|irrelevantDirPath
argument_list|)
expr_stmt|;
name|Path
name|doneAppHomeDir
init|=
operator|new
name|Path
argument_list|(
operator|new
name|Path
argument_list|(
name|TEST_DONE_DIR_PATH
argument_list|,
literal|"0000"
argument_list|)
argument_list|,
literal|"001"
argument_list|)
decl_stmt|;
comment|// First application, untouched after creation
name|Path
name|appDirClean
init|=
operator|new
name|Path
argument_list|(
name|doneAppHomeDir
argument_list|,
name|TEST_APP_DIR_NAME
argument_list|)
decl_stmt|;
name|Path
name|attemptDirClean
init|=
operator|new
name|Path
argument_list|(
name|appDirClean
argument_list|,
name|TEST_ATTEMPT_DIR_NAME
argument_list|)
decl_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|attemptDirClean
argument_list|)
expr_stmt|;
name|Path
name|filePath
init|=
operator|new
name|Path
argument_list|(
name|attemptDirClean
argument_list|,
literal|"test.log"
argument_list|)
decl_stmt|;
name|stream
operator|=
name|fs
operator|.
name|create
argument_list|(
name|filePath
argument_list|)
expr_stmt|;
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Second application, one file touched after creation
name|Path
name|appDirHoldByFile
init|=
operator|new
name|Path
argument_list|(
name|doneAppHomeDir
argument_list|,
name|TEST_APP_DIR_NAME
operator|+
literal|"1"
argument_list|)
decl_stmt|;
name|Path
name|attemptDirHoldByFile
init|=
operator|new
name|Path
argument_list|(
name|appDirHoldByFile
argument_list|,
name|TEST_ATTEMPT_DIR_NAME
argument_list|)
decl_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|attemptDirHoldByFile
argument_list|)
expr_stmt|;
name|Path
name|filePathHold
init|=
operator|new
name|Path
argument_list|(
name|attemptDirHoldByFile
argument_list|,
literal|"test1.log"
argument_list|)
decl_stmt|;
name|stream
operator|=
name|fs
operator|.
name|create
argument_list|(
name|filePathHold
argument_list|)
expr_stmt|;
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Third application, one dir touched after creation
name|Path
name|appDirHoldByDir
init|=
operator|new
name|Path
argument_list|(
name|doneAppHomeDir
argument_list|,
name|TEST_APP_DIR_NAME
operator|+
literal|"2"
argument_list|)
decl_stmt|;
name|Path
name|attemptDirHoldByDir
init|=
operator|new
name|Path
argument_list|(
name|appDirHoldByDir
argument_list|,
name|TEST_ATTEMPT_DIR_NAME
argument_list|)
decl_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|attemptDirHoldByDir
argument_list|)
expr_stmt|;
name|Path
name|dirPathHold
init|=
operator|new
name|Path
argument_list|(
name|attemptDirHoldByDir
argument_list|,
literal|"hold"
argument_list|)
decl_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|dirPathHold
argument_list|)
expr_stmt|;
comment|// Fourth application, empty dirs
name|Path
name|appDirEmpty
init|=
operator|new
name|Path
argument_list|(
name|doneAppHomeDir
argument_list|,
name|TEST_APP_DIR_NAME
operator|+
literal|"3"
argument_list|)
decl_stmt|;
name|Path
name|attemptDirEmpty
init|=
operator|new
name|Path
argument_list|(
name|appDirEmpty
argument_list|,
name|TEST_ATTEMPT_DIR_NAME
argument_list|)
decl_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|attemptDirEmpty
argument_list|)
expr_stmt|;
name|Path
name|dirPathEmpty
init|=
operator|new
name|Path
argument_list|(
name|attemptDirEmpty
argument_list|,
literal|"empty"
argument_list|)
decl_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
name|dirPathEmpty
argument_list|)
expr_stmt|;
comment|// Should retain all logs after this run
name|EntityGroupFSTimelineStore
operator|.
name|cleanLogs
argument_list|(
name|TEST_DONE_DIR_PATH
argument_list|,
name|fs
argument_list|,
literal|10000
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
name|irrelevantDirPath
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
name|irrelevantFilePath
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
name|filePath
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
name|filePathHold
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
name|dirPathHold
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
name|dirPathEmpty
argument_list|)
argument_list|)
expr_stmt|;
comment|// Make sure the created dir is old enough
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
comment|// Touch the second application
name|stream
operator|=
name|fs
operator|.
name|append
argument_list|(
name|filePathHold
argument_list|)
expr_stmt|;
name|stream
operator|.
name|writeBytes
argument_list|(
literal|"append"
argument_list|)
expr_stmt|;
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Touch the third application by creating a new dir
name|fs
operator|.
name|mkdirs
argument_list|(
operator|new
name|Path
argument_list|(
name|dirPathHold
argument_list|,
literal|"holdByMe"
argument_list|)
argument_list|)
expr_stmt|;
name|EntityGroupFSTimelineStore
operator|.
name|cleanLogs
argument_list|(
name|TEST_DONE_DIR_PATH
argument_list|,
name|fs
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
comment|// Verification after the second cleaner call
name|assertTrue
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
name|irrelevantDirPath
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
name|irrelevantFilePath
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
name|filePathHold
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
name|dirPathHold
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
name|doneAppHomeDir
argument_list|)
argument_list|)
expr_stmt|;
comment|// appDirClean and appDirEmpty should be cleaned up
name|assertFalse
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
name|appDirClean
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
name|appDirEmpty
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPluginRead ()
specifier|public
name|void
name|testPluginRead
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Verify precondition
name|assertEquals
argument_list|(
name|EntityGroupPlugInForTest
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|store
operator|.
name|getConfig
argument_list|()
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_ENTITY_GROUP_PLUGIN_CLASSES
argument_list|)
argument_list|)
expr_stmt|;
comment|// Load data and cache item, prepare timeline store by making a cache item
name|EntityGroupFSTimelineStore
operator|.
name|AppLogs
name|appLogs
init|=
name|store
operator|.
expr|new
name|AppLogs
argument_list|(
name|TEST_APPLICATION_ID
argument_list|,
name|TEST_APP_DIR_PATH
argument_list|,
name|AppState
operator|.
name|COMPLETED
argument_list|)
decl_stmt|;
name|EntityCacheItem
name|cacheItem
init|=
operator|new
name|EntityCacheItem
argument_list|(
name|config
argument_list|,
name|fs
argument_list|)
decl_stmt|;
name|cacheItem
operator|.
name|setAppLogs
argument_list|(
name|appLogs
argument_list|)
expr_stmt|;
name|store
operator|.
name|setCachedLogs
argument_list|(
name|EntityGroupPlugInForTest
operator|.
name|getStandardTimelineGroupId
argument_list|()
argument_list|,
name|cacheItem
argument_list|)
expr_stmt|;
comment|// Generate TDM
name|TimelineDataManager
name|tdm
init|=
name|PluginStoreTestUtils
operator|.
name|getTdmWithStore
argument_list|(
name|config
argument_list|,
name|store
argument_list|)
decl_stmt|;
comment|// Verify single entity read
name|TimelineEntity
name|entity3
init|=
name|tdm
operator|.
name|getEntity
argument_list|(
literal|"type_3"
argument_list|,
literal|"id_3"
argument_list|,
name|EnumSet
operator|.
name|allOf
argument_list|(
name|TimelineReader
operator|.
name|Field
operator|.
name|class
argument_list|)
argument_list|,
name|UserGroupInformation
operator|.
name|getLoginUser
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|entity3
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|entityNew
operator|.
name|getStartTime
argument_list|()
argument_list|,
name|entity3
operator|.
name|getStartTime
argument_list|()
argument_list|)
expr_stmt|;
comment|// Verify multiple entities read
name|TimelineEntities
name|entities
init|=
name|tdm
operator|.
name|getEntities
argument_list|(
literal|"type_3"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|EnumSet
operator|.
name|allOf
argument_list|(
name|TimelineReader
operator|.
name|Field
operator|.
name|class
argument_list|)
argument_list|,
name|UserGroupInformation
operator|.
name|getLoginUser
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|entities
operator|.
name|getEntities
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
for|for
control|(
name|TimelineEntity
name|entity
range|:
name|entities
operator|.
name|getEntities
argument_list|()
control|)
block|{
name|assertEquals
argument_list|(
name|entityNew
operator|.
name|getStartTime
argument_list|()
argument_list|,
name|entity
operator|.
name|getStartTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testSummaryRead ()
specifier|public
name|void
name|testSummaryRead
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Load data
name|EntityGroupFSTimelineStore
operator|.
name|AppLogs
name|appLogs
init|=
name|store
operator|.
expr|new
name|AppLogs
argument_list|(
name|TEST_APPLICATION_ID
argument_list|,
name|TEST_APP_DIR_PATH
argument_list|,
name|AppState
operator|.
name|COMPLETED
argument_list|)
decl_stmt|;
name|TimelineDataManager
name|tdm
init|=
name|PluginStoreTestUtils
operator|.
name|getTdmWithStore
argument_list|(
name|config
argument_list|,
name|store
argument_list|)
decl_stmt|;
name|appLogs
operator|.
name|scanForLogs
argument_list|()
expr_stmt|;
name|appLogs
operator|.
name|parseSummaryLogs
argument_list|(
name|tdm
argument_list|)
expr_stmt|;
comment|// Verify single entity read
name|PluginStoreTestUtils
operator|.
name|verifyTestEntities
argument_list|(
name|tdm
argument_list|)
expr_stmt|;
comment|// Verify multiple entities read
name|TimelineEntities
name|entities
init|=
name|tdm
operator|.
name|getEntities
argument_list|(
literal|"type_1"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|EnumSet
operator|.
name|allOf
argument_list|(
name|TimelineReader
operator|.
name|Field
operator|.
name|class
argument_list|)
argument_list|,
name|UserGroupInformation
operator|.
name|getLoginUser
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|entities
operator|.
name|getEntities
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
for|for
control|(
name|TimelineEntity
name|entity
range|:
name|entities
operator|.
name|getEntities
argument_list|()
control|)
block|{
name|assertEquals
argument_list|(
operator|(
name|Long
operator|)
literal|123l
argument_list|,
name|entity
operator|.
name|getStartTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|createTestFiles ()
specifier|private
name|void
name|createTestFiles
parameter_list|()
throws|throws
name|IOException
block|{
name|TimelineEntities
name|entities
init|=
name|PluginStoreTestUtils
operator|.
name|generateTestEntities
argument_list|()
decl_stmt|;
name|PluginStoreTestUtils
operator|.
name|writeEntities
argument_list|(
name|entities
argument_list|,
operator|new
name|Path
argument_list|(
name|TEST_ATTEMPT_DIR_PATH
argument_list|,
name|TEST_SUMMARY_LOG_FILE_NAME
argument_list|)
argument_list|,
name|fs
argument_list|)
expr_stmt|;
name|entityNew
operator|=
name|PluginStoreTestUtils
operator|.
name|createEntity
argument_list|(
literal|"id_3"
argument_list|,
literal|"type_3"
argument_list|,
literal|789l
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|"domain_id_1"
argument_list|)
expr_stmt|;
name|TimelineEntities
name|entityList
init|=
operator|new
name|TimelineEntities
argument_list|()
decl_stmt|;
name|entityList
operator|.
name|addEntity
argument_list|(
name|entityNew
argument_list|)
expr_stmt|;
name|PluginStoreTestUtils
operator|.
name|writeEntities
argument_list|(
name|entityList
argument_list|,
operator|new
name|Path
argument_list|(
name|TEST_ATTEMPT_DIR_PATH
argument_list|,
name|TEST_ENTITY_LOG_FILE_NAME
argument_list|)
argument_list|,
name|fs
argument_list|)
expr_stmt|;
name|FSDataOutputStream
name|out
init|=
name|fs
operator|.
name|create
argument_list|(
operator|new
name|Path
argument_list|(
name|TEST_ATTEMPT_DIR_PATH
argument_list|,
name|TEST_DOMAIN_LOG_FILE_NAME
argument_list|)
argument_list|)
decl_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

