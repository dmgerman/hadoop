begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * *  *  Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements.  See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership.  The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  * /  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.runtime
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
name|linux
operator|.
name|runtime
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
name|yarn
operator|.
name|server
operator|.
name|nodemanager
operator|.
name|containermanager
operator|.
name|linux
operator|.
name|runtime
operator|.
name|runc
operator|.
name|ImageManifest
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
name|nodemanager
operator|.
name|containermanager
operator|.
name|linux
operator|.
name|runtime
operator|.
name|runc
operator|.
name|ImageTagToManifestPlugin
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
name|ObjectMapper
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
name|Assert
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
name|Test
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
name|BufferedReader
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
name|io
operator|.
name|PrintWriter
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
name|NM_HDFS_RUNC_IMAGE_TAG_TO_HASH_FILE
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
name|NM_LOCAL_RUNC_IMAGE_TAG_TO_HASH_FILE
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
name|NM_RUNC_IMAGE_TOPLEVEL_DIR
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

begin_comment
comment|/**  * This class tests the hdfs manifest to resources plugin used by the  * RuncContainerRuntime to map an image manifest into a list of local resources.  */
end_comment

begin_class
DECL|class|TestImageTagToManifestPlugin
specifier|public
class|class
name|TestImageTagToManifestPlugin
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
name|TestImageTagToManifestPlugin
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|mockImageTagToManifestPlugin
specifier|private
name|MockImageTagToManifestPlugin
name|mockImageTagToManifestPlugin
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|tmpPath
specifier|private
name|String
name|tmpPath
init|=
operator|new
name|StringBuffer
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|'/'
argument_list|)
operator|.
name|append
argument_list|(
literal|"hadoop.tmp.dir"
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
DECL|field|mapper
specifier|private
name|ObjectMapper
name|mapper
decl_stmt|;
DECL|field|manifestJson
specifier|private
name|String
name|manifestJson
init|=
literal|"{\n"
operator|+
literal|"   \"schemaVersion\": 2,\n"
operator|+
literal|"   \"mediaType\": \"application/vnd.docker.distribution.manifest.v2+json\",\n"
operator|+
literal|"   \"config\": {\n"
operator|+
literal|"      \"mediaType\": \"application/vnd.docker.container.image.v1+json\",\n"
operator|+
literal|"      \"size\": 2857,\n"
operator|+
literal|"      \"digest\": \"sha256:e23cac476d0238f0f859c1e07e5faad85262bca490ef5c3a9da32a5b39c6b204\"\n"
operator|+
literal|"   },\n"
operator|+
literal|"   \"layers\": [\n"
operator|+
literal|"      {\n"
operator|+
literal|"         \"mediaType\": \"application/vnd.docker.image.rootfs.diff.tar.gzip\",\n"
operator|+
literal|"         \"size\": 185784329,\n"
operator|+
literal|"         \"digest\": \"sha256:e060f9dd9e8cd9ec0e2814b661a96d78f7298120d7654ba9f83ebfb11ff1fb1e\"\n"
operator|+
literal|"      },\n"
operator|+
literal|"      {\n"
operator|+
literal|"         \"mediaType\": \"application/vnd.docker.image.rootfs.diff.tar.gzip\",\n"
operator|+
literal|"         \"size\": 10852,\n"
operator|+
literal|"         \"digest\": \"sha256:5af5ff88469c8473487bfbc2fe81b4e7d84644bd91f1ab9305de47ef5673637e\"\n"
operator|+
literal|"      }\n"
operator|+
literal|"   ]\n"
operator|+
literal|"}"
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|mapper
operator|=
operator|new
name|ObjectMapper
argument_list|()
expr_stmt|;
name|File
name|tmpDir
init|=
operator|new
name|File
argument_list|(
name|tmpPath
argument_list|)
decl_stmt|;
name|tmpDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|cleanUp ()
specifier|public
name|void
name|cleanUp
parameter_list|()
throws|throws
name|IOException
block|{
name|File
name|tmpDir
init|=
operator|new
name|File
argument_list|(
name|tmpPath
argument_list|)
decl_stmt|;
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|tmpDir
argument_list|)
expr_stmt|;
block|}
comment|/**    * This class mocks the hdfs manifest to resources plugin used by the    * RuncContainerRuntime to enable testing.    */
DECL|class|MockImageTagToManifestPlugin
specifier|public
class|class
name|MockImageTagToManifestPlugin
extends|extends
name|ImageTagToManifestPlugin
block|{
DECL|field|mockLocalBufferedReader
specifier|private
name|BufferedReader
name|mockLocalBufferedReader
decl_stmt|;
DECL|field|mockHdfsBufferedReader
specifier|private
name|BufferedReader
name|mockHdfsBufferedReader
decl_stmt|;
DECL|method|MockImageTagToManifestPlugin (BufferedReader mockLocalBufferedReader, BufferedReader mockHdfsBufferedReader)
name|MockImageTagToManifestPlugin
parameter_list|(
name|BufferedReader
name|mockLocalBufferedReader
parameter_list|,
name|BufferedReader
name|mockHdfsBufferedReader
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|mockLocalBufferedReader
operator|=
name|mockLocalBufferedReader
expr_stmt|;
name|this
operator|.
name|mockHdfsBufferedReader
operator|=
name|mockHdfsBufferedReader
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getLocalImageToHashReader ()
specifier|protected
name|BufferedReader
name|getLocalImageToHashReader
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|mockLocalBufferedReader
return|;
block|}
annotation|@
name|Override
DECL|method|getHdfsImageToHashReader ()
specifier|protected
name|BufferedReader
name|getHdfsImageToHashReader
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|mockHdfsBufferedReader
return|;
block|}
block|}
annotation|@
name|Test
DECL|method|testLocalGetHashFromImageTag ()
specifier|public
name|void
name|testLocalGetHashFromImageTag
parameter_list|()
throws|throws
name|IOException
block|{
name|BufferedReader
name|mockLocalBufferedReader
init|=
name|mock
argument_list|(
name|BufferedReader
operator|.
name|class
argument_list|)
decl_stmt|;
name|BufferedReader
name|mockHdfsBufferedReader
init|=
name|mock
argument_list|(
name|BufferedReader
operator|.
name|class
argument_list|)
decl_stmt|;
name|String
name|commentImage
init|=
literal|"commentimage:latest"
decl_stmt|;
name|String
name|commentImageHash
init|=
literal|"142fff813433c1faa8796388db3a1fa1e899ba08c9e42ad2e33c42696d0f15d2"
decl_stmt|;
name|String
name|fakeImageLatest
init|=
literal|"fakeimage:latest"
decl_stmt|;
name|String
name|fakeImageCurrent
init|=
literal|"fakeimage:current"
decl_stmt|;
name|String
name|fakeImageHash
init|=
literal|"f75903872eb2963e158502ef07f2e56d3a2e90a012b4afe3440461b54142a567"
decl_stmt|;
name|String
name|busyboxImage
init|=
literal|"repo/busybox:123"
decl_stmt|;
name|String
name|busyboxHash
init|=
literal|"c6912b9911deceec6c43ebb4c31c96374a8ebb3de4cd75f377dba6c07707de6e"
decl_stmt|;
name|String
name|commentLine
init|=
literal|"#"
operator|+
name|commentImage
operator|+
name|commentImageHash
operator|+
literal|"#2nd comment"
decl_stmt|;
name|String
name|busyboxLine
init|=
name|busyboxImage
operator|+
literal|":"
operator|+
name|busyboxHash
operator|+
literal|"#comment"
decl_stmt|;
name|String
name|fakeImageLine
init|=
name|fakeImageLatest
operator|+
literal|","
operator|+
name|fakeImageCurrent
operator|+
literal|":"
operator|+
name|fakeImageHash
operator|+
literal|"#fakeimage comment"
decl_stmt|;
name|when
argument_list|(
name|mockLocalBufferedReader
operator|.
name|readLine
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|commentLine
argument_list|,
name|fakeImageLine
argument_list|,
name|busyboxLine
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mockImageTagToManifestPlugin
operator|=
operator|new
name|MockImageTagToManifestPlugin
argument_list|(
name|mockLocalBufferedReader
argument_list|,
name|mockHdfsBufferedReader
argument_list|)
expr_stmt|;
name|mockImageTagToManifestPlugin
operator|.
name|loadImageToHashFiles
argument_list|()
expr_stmt|;
name|String
name|returnedFakeImageHash
init|=
name|mockImageTagToManifestPlugin
operator|.
name|getHashFromImageTag
argument_list|(
name|fakeImageLatest
argument_list|)
decl_stmt|;
name|String
name|returnedBusyboxHash
init|=
name|mockImageTagToManifestPlugin
operator|.
name|getHashFromImageTag
argument_list|(
name|busyboxImage
argument_list|)
decl_stmt|;
name|String
name|returnedCommentHash
init|=
name|mockImageTagToManifestPlugin
operator|.
name|getHashFromImageTag
argument_list|(
name|commentImage
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|fakeImageHash
argument_list|,
name|returnedFakeImageHash
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|busyboxHash
argument_list|,
name|returnedBusyboxHash
argument_list|)
expr_stmt|;
comment|//Image hash should not be found, so returned hash should be the tag
name|Assert
operator|.
name|assertEquals
argument_list|(
name|commentImage
argument_list|,
name|returnedCommentHash
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testHdfsGetHashFromImageTag ()
specifier|public
name|void
name|testHdfsGetHashFromImageTag
parameter_list|()
throws|throws
name|IOException
block|{
name|BufferedReader
name|mockLocalBufferedReader
init|=
name|mock
argument_list|(
name|BufferedReader
operator|.
name|class
argument_list|)
decl_stmt|;
name|BufferedReader
name|mockHdfsBufferedReader
init|=
name|mock
argument_list|(
name|BufferedReader
operator|.
name|class
argument_list|)
decl_stmt|;
name|String
name|commentImage
init|=
literal|"commentimage:latest"
decl_stmt|;
name|String
name|commentImageHash
init|=
literal|"142fff813433c1faa8796388db3a1fa1e899ba08c9e42ad2e33c42696d0f15d2"
decl_stmt|;
name|String
name|fakeImageLatest
init|=
literal|"fakeimage:latest"
decl_stmt|;
name|String
name|fakeImageCurrent
init|=
literal|"fakeimage:current"
decl_stmt|;
name|String
name|fakeImageHash
init|=
literal|"f75903872eb2963e158502ef07f2e56d3a2e90a012b4afe3440461b54142a567"
decl_stmt|;
name|String
name|busyboxImage
init|=
literal|"repo/busybox:123"
decl_stmt|;
name|String
name|busyboxHash
init|=
literal|"c6912b9911deceec6c43ebb4c31c96374a8ebb3de4cd75f377dba6c07707de6e"
decl_stmt|;
name|String
name|commentLine
init|=
literal|"#"
operator|+
name|commentImage
operator|+
name|commentImageHash
operator|+
literal|"#2nd comment"
decl_stmt|;
name|String
name|busyboxLine
init|=
name|busyboxImage
operator|+
literal|":"
operator|+
name|busyboxHash
operator|+
literal|"#comment"
decl_stmt|;
name|String
name|fakeImageLine
init|=
name|fakeImageLatest
operator|+
literal|","
operator|+
name|fakeImageCurrent
operator|+
literal|":"
operator|+
name|fakeImageHash
operator|+
literal|"#fakeimage comment"
decl_stmt|;
name|when
argument_list|(
name|mockHdfsBufferedReader
operator|.
name|readLine
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|commentLine
argument_list|,
name|fakeImageLine
argument_list|,
name|busyboxLine
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mockImageTagToManifestPlugin
operator|=
operator|new
name|MockImageTagToManifestPlugin
argument_list|(
name|mockLocalBufferedReader
argument_list|,
name|mockHdfsBufferedReader
argument_list|)
expr_stmt|;
name|mockImageTagToManifestPlugin
operator|.
name|loadImageToHashFiles
argument_list|()
expr_stmt|;
name|String
name|returnedFakeImageHash
init|=
name|mockImageTagToManifestPlugin
operator|.
name|getHashFromImageTag
argument_list|(
name|fakeImageLatest
argument_list|)
decl_stmt|;
name|String
name|returnedBusyboxHash
init|=
name|mockImageTagToManifestPlugin
operator|.
name|getHashFromImageTag
argument_list|(
name|busyboxImage
argument_list|)
decl_stmt|;
name|String
name|returnedCommentHash
init|=
name|mockImageTagToManifestPlugin
operator|.
name|getHashFromImageTag
argument_list|(
name|commentImage
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|fakeImageHash
argument_list|,
name|returnedFakeImageHash
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|busyboxHash
argument_list|,
name|returnedBusyboxHash
argument_list|)
expr_stmt|;
comment|//Image hash should not be found, so returned hash should be the tag
name|Assert
operator|.
name|assertEquals
argument_list|(
name|commentImage
argument_list|,
name|returnedCommentHash
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetManifestFromImageTag ()
specifier|public
name|void
name|testGetManifestFromImageTag
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|manifestPath
init|=
name|tmpPath
operator|+
literal|"/manifests"
decl_stmt|;
name|File
name|manifestDir
init|=
operator|new
name|File
argument_list|(
name|manifestPath
argument_list|)
decl_stmt|;
name|manifestDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|NM_LOCAL_RUNC_IMAGE_TAG_TO_HASH_FILE
argument_list|,
literal|"local-image-tag-to-hash"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|NM_HDFS_RUNC_IMAGE_TAG_TO_HASH_FILE
argument_list|,
literal|"hdfs-image-tag-to-hash"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|NM_RUNC_IMAGE_TOPLEVEL_DIR
argument_list|,
name|tmpPath
argument_list|)
expr_stmt|;
name|String
name|manifestHash
init|=
literal|"d0e8c542d28e8e868848aeb58beecb31079eb7ada1293c4bc2eded08daed605a"
decl_stmt|;
name|PrintWriter
name|printWriter
init|=
operator|new
name|PrintWriter
argument_list|(
name|manifestPath
operator|+
literal|"/"
operator|+
name|manifestHash
argument_list|)
decl_stmt|;
name|printWriter
operator|.
name|println
argument_list|(
name|manifestJson
argument_list|)
expr_stmt|;
name|printWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|BufferedReader
name|mockLocalBufferedReader
init|=
name|mock
argument_list|(
name|BufferedReader
operator|.
name|class
argument_list|)
decl_stmt|;
name|BufferedReader
name|mockHdfsBufferedReader
init|=
name|mock
argument_list|(
name|BufferedReader
operator|.
name|class
argument_list|)
decl_stmt|;
name|mockImageTagToManifestPlugin
operator|=
operator|new
name|MockImageTagToManifestPlugin
argument_list|(
name|mockLocalBufferedReader
argument_list|,
name|mockHdfsBufferedReader
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|String
name|getHashFromImageTag
parameter_list|(
name|String
name|imageTag
parameter_list|)
block|{
return|return
name|manifestHash
return|;
block|}
block|}
expr_stmt|;
name|mockImageTagToManifestPlugin
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|ImageManifest
name|manifest
init|=
name|mockImageTagToManifestPlugin
operator|.
name|getManifestFromImageTag
argument_list|(
literal|"image"
argument_list|)
decl_stmt|;
name|ImageManifest
name|expectedManifest
init|=
name|mapper
operator|.
name|readValue
argument_list|(
name|manifestJson
argument_list|,
name|ImageManifest
operator|.
name|class
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedManifest
operator|.
name|toString
argument_list|()
argument_list|,
name|manifest
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

