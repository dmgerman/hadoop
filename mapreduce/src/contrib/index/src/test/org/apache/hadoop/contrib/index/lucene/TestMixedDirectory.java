begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.contrib.index.lucene
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
name|lucene
package|;
end_package

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
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|analysis
operator|.
name|standard
operator|.
name|StandardAnalyzer
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
name|document
operator|.
name|Field
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
name|IndexDeletionPolicy
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|IndexOutput
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
name|RAMDirectory
import|;
end_import

begin_class
DECL|class|TestMixedDirectory
specifier|public
class|class
name|TestMixedDirectory
extends|extends
name|TestCase
block|{
DECL|field|numDocsPerUpdate
specifier|private
name|int
name|numDocsPerUpdate
init|=
literal|10
decl_stmt|;
DECL|field|maxBufferedDocs
specifier|private
name|int
name|maxBufferedDocs
init|=
literal|2
decl_stmt|;
DECL|method|testMixedDirectoryAndPolicy ()
specifier|public
name|void
name|testMixedDirectoryAndPolicy
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|readDir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|updateIndex
argument_list|(
name|readDir
argument_list|,
literal|0
argument_list|,
name|numDocsPerUpdate
argument_list|,
operator|new
name|KeepOnlyLastCommitDeletionPolicy
argument_list|()
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|readDir
argument_list|,
name|numDocsPerUpdate
argument_list|)
expr_stmt|;
name|IndexOutput
name|out
init|=
name|readDir
operator|.
name|createOutput
argument_list|(
literal|"_"
operator|+
operator|(
name|numDocsPerUpdate
operator|/
name|maxBufferedDocs
operator|+
literal|2
operator|)
operator|+
literal|".cfs"
argument_list|)
decl_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|Directory
name|writeDir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|Directory
name|mixedDir
init|=
operator|new
name|MixedDirectory
argument_list|(
name|readDir
argument_list|,
name|writeDir
argument_list|)
decl_stmt|;
name|updateIndex
argument_list|(
name|mixedDir
argument_list|,
name|numDocsPerUpdate
argument_list|,
name|numDocsPerUpdate
argument_list|,
operator|new
name|MixedDeletionPolicy
argument_list|()
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|readDir
argument_list|,
name|numDocsPerUpdate
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mixedDir
argument_list|,
literal|2
operator|*
name|numDocsPerUpdate
argument_list|)
expr_stmt|;
block|}
DECL|method|updateIndex (Directory dir, int base, int numDocs, IndexDeletionPolicy policy)
specifier|public
name|void
name|updateIndex
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|int
name|base
parameter_list|,
name|int
name|numDocs
parameter_list|,
name|IndexDeletionPolicy
name|policy
parameter_list|)
throws|throws
name|IOException
block|{
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
operator|new
name|StandardAnalyzer
argument_list|()
argument_list|,
name|policy
argument_list|)
decl_stmt|;
name|writer
operator|.
name|setMaxBufferedDocs
argument_list|(
name|maxBufferedDocs
argument_list|)
expr_stmt|;
name|writer
operator|.
name|setMergeFactor
argument_list|(
literal|1000
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
name|numDocs
condition|;
name|i
operator|++
control|)
block|{
name|addDoc
argument_list|(
name|writer
argument_list|,
name|base
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|addDoc (IndexWriter writer, int id)
specifier|private
name|void
name|addDoc
parameter_list|(
name|IndexWriter
name|writer
parameter_list|,
name|int
name|id
parameter_list|)
throws|throws
name|IOException
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"id"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|id
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|UN_TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"content"
argument_list|,
literal|"apache"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
DECL|method|verify (Directory dir, int expectedHits)
specifier|private
name|void
name|verify
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|int
name|expectedHits
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|dir
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
name|int
name|numHits
init|=
name|hits
operator|.
name|length
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|expectedHits
argument_list|,
name|numHits
argument_list|)
expr_stmt|;
name|int
index|[]
name|docs
init|=
operator|new
name|int
index|[
name|numHits
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
name|numHits
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|hit
init|=
name|hits
operator|.
name|doc
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|docs
index|[
name|Integer
operator|.
name|parseInt
argument_list|(
name|hit
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
name|numHits
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|docs
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

