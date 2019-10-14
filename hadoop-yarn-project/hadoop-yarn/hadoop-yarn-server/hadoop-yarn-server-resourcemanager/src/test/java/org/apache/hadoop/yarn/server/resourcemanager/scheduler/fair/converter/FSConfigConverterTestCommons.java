begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *     http://www.apache.org/licenses/LICENSE-2.0  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.converter
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
name|resourcemanager
operator|.
name|scheduler
operator|.
name|fair
operator|.
name|converter
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
name|commons
operator|.
name|lang3
operator|.
name|StringUtils
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
name|resourcemanager
operator|.
name|scheduler
operator|.
name|fair
operator|.
name|FairSchedulerConfiguration
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
name|FileWriter
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
name|PrintStream
import|;
end_import

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

begin_comment
comment|/**  * Helper methods for FS->CS converter testing.  *  */
end_comment

begin_class
DECL|class|FSConfigConverterTestCommons
specifier|public
class|class
name|FSConfigConverterTestCommons
block|{
DECL|field|TEST_DIR
specifier|private
specifier|final
specifier|static
name|String
name|TEST_DIR
init|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|,
literal|"/tmp"
argument_list|)
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
DECL|field|FS_ALLOC_FILE
specifier|public
specifier|final
specifier|static
name|String
name|FS_ALLOC_FILE
init|=
operator|new
name|File
argument_list|(
name|TEST_DIR
argument_list|,
literal|"test-fair-scheduler.xml"
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
DECL|field|YARN_SITE_XML
specifier|public
specifier|final
specifier|static
name|String
name|YARN_SITE_XML
init|=
operator|new
name|File
argument_list|(
name|TEST_DIR
argument_list|,
literal|"test-yarn-site.xml"
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
DECL|field|CONVERSION_RULES_FILE
specifier|public
specifier|final
specifier|static
name|String
name|CONVERSION_RULES_FILE
init|=
operator|new
name|File
argument_list|(
name|TEST_DIR
argument_list|,
literal|"test-conversion-rules.properties"
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
DECL|field|OUTPUT_DIR
specifier|public
specifier|final
specifier|static
name|String
name|OUTPUT_DIR
init|=
operator|new
name|File
argument_list|(
name|TEST_DIR
argument_list|,
literal|"conversion-output"
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
DECL|field|outContent
specifier|private
specifier|final
name|ByteArrayOutputStream
name|outContent
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
DECL|field|errContent
specifier|private
specifier|final
name|ByteArrayOutputStream
name|errContent
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
DECL|field|originalOutStream
specifier|private
name|PrintStream
name|originalOutStream
decl_stmt|;
DECL|field|originalErrStream
specifier|private
name|PrintStream
name|originalErrStream
decl_stmt|;
DECL|method|FSConfigConverterTestCommons ()
specifier|public
name|FSConfigConverterTestCommons
parameter_list|()
block|{
name|saveOriginalStreams
argument_list|()
expr_stmt|;
name|replaceStreamsWithByteArrays
argument_list|()
expr_stmt|;
block|}
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|IOException
block|{
name|File
name|d
init|=
operator|new
name|File
argument_list|(
name|TEST_DIR
argument_list|,
literal|"conversion-output"
argument_list|)
decl_stmt|;
if|if
condition|(
name|d
operator|.
name|exists
argument_list|()
condition|)
block|{
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|d
argument_list|)
expr_stmt|;
block|}
name|boolean
name|success
init|=
name|d
operator|.
name|mkdirs
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Can't create directory: "
operator|+
name|d
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|success
argument_list|)
expr_stmt|;
block|}
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
block|{
name|deleteTestFiles
argument_list|()
expr_stmt|;
name|restoreStreams
argument_list|()
expr_stmt|;
block|}
DECL|method|saveOriginalStreams ()
specifier|private
name|void
name|saveOriginalStreams
parameter_list|()
block|{
name|originalOutStream
operator|=
name|System
operator|.
name|out
expr_stmt|;
name|originalErrStream
operator|=
name|System
operator|.
name|err
expr_stmt|;
block|}
DECL|method|replaceStreamsWithByteArrays ()
specifier|private
name|void
name|replaceStreamsWithByteArrays
parameter_list|()
block|{
name|System
operator|.
name|setOut
argument_list|(
operator|new
name|PrintStream
argument_list|(
name|outContent
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|setErr
argument_list|(
operator|new
name|PrintStream
argument_list|(
name|errContent
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|restoreStreams ()
specifier|private
name|void
name|restoreStreams
parameter_list|()
block|{
name|System
operator|.
name|setOut
argument_list|(
name|originalOutStream
argument_list|)
expr_stmt|;
name|System
operator|.
name|setErr
argument_list|(
name|originalErrStream
argument_list|)
expr_stmt|;
block|}
DECL|method|getErrContent ()
name|ByteArrayOutputStream
name|getErrContent
parameter_list|()
block|{
return|return
name|errContent
return|;
block|}
DECL|method|deleteTestFiles ()
specifier|private
name|void
name|deleteTestFiles
parameter_list|()
block|{
comment|//Files may not be created so we are not strict here!
name|deleteFile
argument_list|(
name|FS_ALLOC_FILE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|deleteFile
argument_list|(
name|YARN_SITE_XML
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|deleteFile
argument_list|(
name|CONVERSION_RULES_FILE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|deleteFile
argument_list|(
name|OUTPUT_DIR
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|deleteFile (String f, boolean strict)
specifier|private
specifier|static
name|void
name|deleteFile
parameter_list|(
name|String
name|f
parameter_list|,
name|boolean
name|strict
parameter_list|)
block|{
name|boolean
name|delete
init|=
operator|new
name|File
argument_list|(
name|f
argument_list|)
operator|.
name|delete
argument_list|()
decl_stmt|;
if|if
condition|(
name|strict
operator|&&
operator|!
name|delete
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Can't delete test file: "
operator|+
name|f
argument_list|)
throw|;
block|}
block|}
DECL|method|setupFSConfigConversionFiles ()
specifier|public
specifier|static
name|void
name|setupFSConfigConversionFiles
parameter_list|()
throws|throws
name|IOException
block|{
name|configureFairSchedulerXml
argument_list|()
expr_stmt|;
name|configureYarnSiteXmlWithFsAllocFileDefined
argument_list|()
expr_stmt|;
name|configureDummyConversionRulesFile
argument_list|()
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"checkstyle:linelength"
argument_list|)
DECL|method|configureFairSchedulerXml ()
specifier|public
specifier|static
name|void
name|configureFairSchedulerXml
parameter_list|()
throws|throws
name|IOException
block|{
name|PrintWriter
name|out
init|=
operator|new
name|PrintWriter
argument_list|(
operator|new
name|FileWriter
argument_list|(
name|FS_ALLOC_FILE
argument_list|)
argument_list|)
decl_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<?xml version=\"1.0\"?>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<allocations>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<queueMaxAMShareDefault>-1.0</queueMaxAMShareDefault>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<defaultQueueSchedulingPolicy>fair</defaultQueueSchedulingPolicy>"
argument_list|)
expr_stmt|;
name|addQueue
argument_list|(
name|out
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"</allocations>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"checkstyle:linelength"
argument_list|)
DECL|method|addQueue (PrintWriter out, String additionalConfig)
specifier|private
specifier|static
name|void
name|addQueue
parameter_list|(
name|PrintWriter
name|out
parameter_list|,
name|String
name|additionalConfig
parameter_list|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"<queue name=\"root\">"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<schedulingPolicy>fair</schedulingPolicy>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<weight>1.0</weight>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<fairSharePreemptionTimeout>100</fairSharePreemptionTimeout>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<minSharePreemptionTimeout>120</minSharePreemptionTimeout>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<fairSharePreemptionThreshold>.5</fairSharePreemptionThreshold>"
argument_list|)
expr_stmt|;
if|if
condition|(
name|StringUtils
operator|.
name|isNotEmpty
argument_list|(
name|additionalConfig
argument_list|)
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
name|additionalConfig
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|println
argument_list|(
literal|"</queue>"
argument_list|)
expr_stmt|;
block|}
DECL|method|configureEmptyFairSchedulerXml ()
specifier|public
specifier|static
name|void
name|configureEmptyFairSchedulerXml
parameter_list|()
throws|throws
name|IOException
block|{
name|PrintWriter
name|out
init|=
operator|new
name|PrintWriter
argument_list|(
operator|new
name|FileWriter
argument_list|(
name|FS_ALLOC_FILE
argument_list|)
argument_list|)
decl_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<?xml version=\"1.0\"?>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<allocations></allocations>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|configureYarnSiteXmlWithFsAllocFileDefined ()
specifier|public
specifier|static
name|void
name|configureYarnSiteXmlWithFsAllocFileDefined
parameter_list|()
throws|throws
name|IOException
block|{
name|PrintWriter
name|out
init|=
operator|new
name|PrintWriter
argument_list|(
operator|new
name|FileWriter
argument_list|(
name|YARN_SITE_XML
argument_list|)
argument_list|)
decl_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<?xml version=\"1.0\"?>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<property>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<name>"
operator|+
name|FairSchedulerConfiguration
operator|.
name|ALLOCATION_FILE
operator|+
literal|"</name>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<value>"
operator|+
name|FS_ALLOC_FILE
operator|+
literal|"</value>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"</property>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|configureEmptyYarnSiteXml ()
specifier|public
specifier|static
name|void
name|configureEmptyYarnSiteXml
parameter_list|()
throws|throws
name|IOException
block|{
name|PrintWriter
name|out
init|=
operator|new
name|PrintWriter
argument_list|(
operator|new
name|FileWriter
argument_list|(
name|YARN_SITE_XML
argument_list|)
argument_list|)
decl_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<?xml version=\"1.0\"?>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<property></property>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|configureDummyConversionRulesFile ()
specifier|public
specifier|static
name|void
name|configureDummyConversionRulesFile
parameter_list|()
throws|throws
name|IOException
block|{
name|PrintWriter
name|out
init|=
operator|new
name|PrintWriter
argument_list|(
operator|new
name|FileWriter
argument_list|(
name|CONVERSION_RULES_FILE
argument_list|)
argument_list|)
decl_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"dummy_key=dummy_value"
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|configureInvalidConversionRulesFile ()
specifier|public
specifier|static
name|void
name|configureInvalidConversionRulesFile
parameter_list|()
throws|throws
name|IOException
block|{
name|PrintWriter
name|out
init|=
operator|new
name|PrintWriter
argument_list|(
operator|new
name|FileWriter
argument_list|(
name|CONVERSION_RULES_FILE
argument_list|)
argument_list|)
decl_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"bla"
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|configureEmptyConversionRulesFile ()
specifier|public
specifier|static
name|void
name|configureEmptyConversionRulesFile
parameter_list|()
throws|throws
name|IOException
block|{
name|PrintWriter
name|out
init|=
operator|new
name|PrintWriter
argument_list|(
operator|new
name|FileWriter
argument_list|(
name|CONVERSION_RULES_FILE
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

