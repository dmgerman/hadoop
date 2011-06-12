begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.contrib.index.mapred
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|contrib
operator|.
name|index
operator|.
name|mapred
package|;
end_package

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
name|text
operator|.
name|NumberFormat
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
name|contrib
operator|.
name|index
operator|.
name|lucene
operator|.
name|FileSystemDirectory
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
name|PathFilter
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
name|MiniMRCluster
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Document
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexReader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexWriter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|KeepOnlyLastCommitDeletionPolicy
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|MultiReader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|Term
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|Hits
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|IndexSearcher
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|TermQuery
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|Directory
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_class
DECL|class|TestIndexUpdater
specifier|public
class|class
name|TestIndexUpdater
extends|extends
name|TestCase
block|{
DECL|field|NUMBER_FORMAT
specifier|private
specifier|static
specifier|final
name|NumberFormat
name|NUMBER_FORMAT
init|=
name|NumberFormat
operator|.
name|getInstance
argument_list|()
decl_stmt|;
static|static
block|{
name|NUMBER_FORMAT
operator|.
name|setMinimumIntegerDigits
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|NUMBER_FORMAT
operator|.
name|setGroupingUsed
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
comment|// however, "we only allow 0 or 1 reducer in local mode" - from
comment|// LocalJobRunner
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|localInputPath
specifier|private
name|Path
name|localInputPath
init|=
operator|new
name|Path
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"build.test"
argument_list|)
operator|+
literal|"/sample/data.txt"
argument_list|)
decl_stmt|;
DECL|field|inputPath
specifier|private
name|Path
name|inputPath
init|=
operator|new
name|Path
argument_list|(
literal|"/myexample/data.txt"
argument_list|)
decl_stmt|;
DECL|field|outputPath
specifier|private
name|Path
name|outputPath
init|=
operator|new
name|Path
argument_list|(
literal|"/myoutput"
argument_list|)
decl_stmt|;
DECL|field|indexPath
specifier|private
name|Path
name|indexPath
init|=
operator|new
name|Path
argument_list|(
literal|"/myindex"
argument_list|)
decl_stmt|;
DECL|field|initNumShards
specifier|private
name|int
name|initNumShards
init|=
literal|3
decl_stmt|;
DECL|field|numMapTasks
specifier|private
name|int
name|numMapTasks
init|=
literal|5
decl_stmt|;
DECL|field|numDataNodes
specifier|private
name|int
name|numDataNodes
init|=
literal|3
decl_stmt|;
DECL|field|numTaskTrackers
specifier|private
name|int
name|numTaskTrackers
init|=
literal|3
decl_stmt|;
DECL|field|numRuns
specifier|private
name|int
name|numRuns
init|=
literal|3
decl_stmt|;
DECL|field|numDocsPerRun
specifier|private
name|int
name|numDocsPerRun
init|=
literal|10
decl_stmt|;
comment|// num of docs in local input path
DECL|field|fs
specifier|private
name|FileSystem
name|fs
decl_stmt|;
DECL|field|dfsCluster
specifier|private
name|MiniDFSCluster
name|dfsCluster
decl_stmt|;
DECL|field|mrCluster
specifier|private
name|MiniMRCluster
name|mrCluster
decl_stmt|;
DECL|method|TestIndexUpdater ()
specifier|public
name|TestIndexUpdater
parameter_list|()
throws|throws
name|IOException
block|{
name|super
argument_list|()
expr_stmt|;
if|if
condition|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"hadoop.log.dir"
argument_list|)
operator|==
literal|null
condition|)
block|{
name|String
name|base
init|=
operator|new
name|File
argument_list|(
literal|"."
argument_list|)
operator|.
name|getPath
argument_list|()
decl_stmt|;
comment|// getAbsolutePath();
name|System
operator|.
name|setProperty
argument_list|(
literal|"hadoop.log.dir"
argument_list|,
operator|new
name|Path
argument_list|(
name|base
argument_list|)
operator|.
name|toString
argument_list|()
operator|+
literal|"/logs"
argument_list|)
expr_stmt|;
block|}
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
comment|//See MAPREDUCE-947 for more details. Setting to false prevents the creation of _SUCCESS.
name|conf
operator|.
name|setBoolean
argument_list|(
literal|"mapreduce.fileoutputcommitter.marksuccessfuljobs"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|setUp ()
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
try|try
block|{
name|dfsCluster
operator|=
operator|new
name|MiniDFSCluster
argument_list|(
name|conf
argument_list|,
name|numDataNodes
argument_list|,
literal|true
argument_list|,
operator|(
name|String
index|[]
operator|)
literal|null
argument_list|)
expr_stmt|;
name|fs
operator|=
name|dfsCluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
if|if
condition|(
name|fs
operator|.
name|exists
argument_list|(
name|inputPath
argument_list|)
condition|)
block|{
name|fs
operator|.
name|delete
argument_list|(
name|inputPath
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|fs
operator|.
name|copyFromLocalFile
argument_list|(
name|localInputPath
argument_list|,
name|inputPath
argument_list|)
expr_stmt|;
if|if
condition|(
name|fs
operator|.
name|exists
argument_list|(
name|outputPath
argument_list|)
condition|)
block|{
comment|// do not create, mapred will create
name|fs
operator|.
name|delete
argument_list|(
name|outputPath
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fs
operator|.
name|exists
argument_list|(
name|indexPath
argument_list|)
condition|)
block|{
name|fs
operator|.
name|delete
argument_list|(
name|indexPath
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|mrCluster
operator|=
operator|new
name|MiniMRCluster
argument_list|(
name|numTaskTrackers
argument_list|,
name|fs
operator|.
name|getUri
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
if|if
condition|(
name|dfsCluster
operator|!=
literal|null
condition|)
block|{
name|dfsCluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|dfsCluster
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|fs
operator|!=
literal|null
condition|)
block|{
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
name|fs
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|mrCluster
operator|!=
literal|null
condition|)
block|{
name|mrCluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|mrCluster
operator|=
literal|null
expr_stmt|;
block|}
throw|throw
name|e
throw|;
block|}
block|}
DECL|method|tearDown ()
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|dfsCluster
operator|!=
literal|null
condition|)
block|{
name|dfsCluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|dfsCluster
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|fs
operator|!=
literal|null
condition|)
block|{
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
name|fs
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|mrCluster
operator|!=
literal|null
condition|)
block|{
name|mrCluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|mrCluster
operator|=
literal|null
expr_stmt|;
block|}
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|testIndexUpdater ()
specifier|public
name|void
name|testIndexUpdater
parameter_list|()
throws|throws
name|IOException
block|{
name|IndexUpdateConfiguration
name|iconf
init|=
operator|new
name|IndexUpdateConfiguration
argument_list|(
name|conf
argument_list|)
decl_stmt|;
comment|// max field length, compound file and number of segments will be checked
comment|// later
name|iconf
operator|.
name|setIndexMaxFieldLength
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|iconf
operator|.
name|setIndexUseCompoundFile
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|iconf
operator|.
name|setIndexMaxNumSegments
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|iconf
operator|.
name|setMaxRAMSizeInBytes
argument_list|(
literal|20480
argument_list|)
expr_stmt|;
name|long
name|versionNumber
init|=
operator|-
literal|1
decl_stmt|;
name|long
name|generation
init|=
operator|-
literal|1
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
name|numRuns
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|fs
operator|.
name|exists
argument_list|(
name|outputPath
argument_list|)
condition|)
block|{
name|fs
operator|.
name|delete
argument_list|(
name|outputPath
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|Shard
index|[]
name|shards
init|=
operator|new
name|Shard
index|[
name|initNumShards
operator|+
name|i
index|]
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|shards
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|shards
index|[
name|j
index|]
operator|=
operator|new
name|Shard
argument_list|(
name|versionNumber
argument_list|,
operator|new
name|Path
argument_list|(
name|indexPath
argument_list|,
name|NUMBER_FORMAT
operator|.
name|format
argument_list|(
name|j
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|generation
argument_list|)
expr_stmt|;
block|}
name|run
argument_list|(
name|i
operator|+
literal|1
argument_list|,
name|shards
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|run (int numRuns, Shard[] shards)
specifier|private
name|void
name|run
parameter_list|(
name|int
name|numRuns
parameter_list|,
name|Shard
index|[]
name|shards
parameter_list|)
throws|throws
name|IOException
block|{
name|IIndexUpdater
name|updater
init|=
operator|new
name|IndexUpdater
argument_list|()
decl_stmt|;
name|updater
operator|.
name|run
argument_list|(
name|conf
argument_list|,
operator|new
name|Path
index|[]
block|{
name|inputPath
block|}
argument_list|,
name|outputPath
argument_list|,
name|numMapTasks
argument_list|,
name|shards
argument_list|)
expr_stmt|;
comment|// verify the done files
name|Path
index|[]
name|doneFileNames
init|=
operator|new
name|Path
index|[
name|shards
operator|.
name|length
index|]
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
name|FileStatus
index|[]
name|fileStatus
init|=
name|fs
operator|.
name|listStatus
argument_list|(
name|outputPath
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
name|fileStatus
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|FileStatus
index|[]
name|doneFiles
init|=
name|fs
operator|.
name|listStatus
argument_list|(
name|fileStatus
index|[
name|i
index|]
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|doneFiles
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|doneFileNames
index|[
name|count
operator|++
index|]
operator|=
name|doneFiles
index|[
name|j
index|]
operator|.
name|getPath
argument_list|()
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
name|shards
operator|.
name|length
argument_list|,
name|count
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|count
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
name|doneFileNames
index|[
name|i
index|]
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
name|IndexUpdateReducer
operator|.
name|DONE
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// verify the index
name|IndexReader
index|[]
name|readers
init|=
operator|new
name|IndexReader
index|[
name|shards
operator|.
name|length
index|]
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
name|shards
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Directory
name|dir
init|=
operator|new
name|FileSystemDirectory
argument_list|(
name|fs
argument_list|,
operator|new
name|Path
argument_list|(
name|shards
index|[
name|i
index|]
operator|.
name|getDirectory
argument_list|()
argument_list|)
argument_list|,
literal|false
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|readers
index|[
name|i
index|]
operator|=
name|IndexReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
expr_stmt|;
block|}
name|IndexReader
name|reader
init|=
operator|new
name|MultiReader
argument_list|(
name|readers
argument_list|)
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|Hits
name|hits
init|=
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"apache"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|numRuns
operator|*
name|numDocsPerRun
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|int
index|[]
name|counts
init|=
operator|new
name|int
index|[
name|numDocsPerRun
index|]
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
name|hits
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|doc
init|=
name|hits
operator|.
name|doc
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|counts
index|[
name|Integer
operator|.
name|parseInt
argument_list|(
name|doc
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
argument_list|)
index|]
operator|++
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numDocsPerRun
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|numRuns
argument_list|,
name|counts
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
comment|// max field length is 2, so "dot" is also indexed but not "org"
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"dot"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|numRuns
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|hits
operator|=
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"org"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|hits
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// open and close an index writer with KeepOnlyLastCommitDeletionPolicy
comment|// to remove earlier checkpoints
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|shards
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Directory
name|dir
init|=
operator|new
name|FileSystemDirectory
argument_list|(
name|fs
argument_list|,
operator|new
name|Path
argument_list|(
name|shards
index|[
name|i
index|]
operator|.
name|getDirectory
argument_list|()
argument_list|)
argument_list|,
literal|false
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|,
operator|new
name|KeepOnlyLastCommitDeletionPolicy
argument_list|()
argument_list|)
decl_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// verify the number of segments, must be done after an writer with
comment|// KeepOnlyLastCommitDeletionPolicy so that earlier checkpoints are removed
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|shards
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|PathFilter
name|cfsFilter
init|=
operator|new
name|PathFilter
argument_list|()
block|{
specifier|public
name|boolean
name|accept
parameter_list|(
name|Path
name|path
parameter_list|)
block|{
return|return
name|path
operator|.
name|getName
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|".cfs"
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|FileStatus
index|[]
name|cfsFiles
init|=
name|fs
operator|.
name|listStatus
argument_list|(
operator|new
name|Path
argument_list|(
name|shards
index|[
name|i
index|]
operator|.
name|getDirectory
argument_list|()
argument_list|)
argument_list|,
name|cfsFilter
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|cfsFiles
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

