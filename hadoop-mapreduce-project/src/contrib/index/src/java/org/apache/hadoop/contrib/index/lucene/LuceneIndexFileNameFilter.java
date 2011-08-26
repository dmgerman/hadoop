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
name|lucene
operator|.
name|index
operator|.
name|IndexFileNameFilter
import|;
end_import

begin_comment
comment|/**  * A wrapper class to convert an IndexFileNameFilter which implements  * java.io.FilenameFilter to an org.apache.hadoop.fs.PathFilter.  */
end_comment

begin_class
DECL|class|LuceneIndexFileNameFilter
class|class
name|LuceneIndexFileNameFilter
implements|implements
name|PathFilter
block|{
DECL|field|singleton
specifier|private
specifier|static
specifier|final
name|LuceneIndexFileNameFilter
name|singleton
init|=
operator|new
name|LuceneIndexFileNameFilter
argument_list|()
decl_stmt|;
comment|/**    * Get a static instance.    * @return the static instance    */
DECL|method|getFilter ()
specifier|public
specifier|static
name|LuceneIndexFileNameFilter
name|getFilter
parameter_list|()
block|{
return|return
name|singleton
return|;
block|}
DECL|field|luceneFilter
specifier|private
specifier|final
name|IndexFileNameFilter
name|luceneFilter
decl_stmt|;
DECL|method|LuceneIndexFileNameFilter ()
specifier|private
name|LuceneIndexFileNameFilter
parameter_list|()
block|{
name|luceneFilter
operator|=
name|IndexFileNameFilter
operator|.
name|getFilter
argument_list|()
expr_stmt|;
block|}
comment|/* (non-Javadoc)    * @see org.apache.hadoop.fs.PathFilter#accept(org.apache.hadoop.fs.Path)    */
DECL|method|accept (Path path)
specifier|public
name|boolean
name|accept
parameter_list|(
name|Path
name|path
parameter_list|)
block|{
return|return
name|luceneFilter
operator|.
name|accept
argument_list|(
literal|null
argument_list|,
name|path
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

