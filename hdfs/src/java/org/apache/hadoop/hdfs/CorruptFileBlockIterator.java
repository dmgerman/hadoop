begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
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
name|java
operator|.
name|util
operator|.
name|NoSuchElementException
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
name|protocol
operator|.
name|CorruptFileBlocks
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
name|RemoteIterator
import|;
end_import

begin_comment
comment|/**  * Provides an iterator interface for listCorruptFileBlocks.  * This class is used by DistributedFileSystem and Hdfs.  */
end_comment

begin_class
DECL|class|CorruptFileBlockIterator
specifier|public
class|class
name|CorruptFileBlockIterator
implements|implements
name|RemoteIterator
argument_list|<
name|Path
argument_list|>
block|{
DECL|field|dfs
specifier|private
specifier|final
name|DFSClient
name|dfs
decl_stmt|;
DECL|field|path
specifier|private
name|String
name|path
decl_stmt|;
DECL|field|files
specifier|private
name|String
index|[]
name|files
init|=
literal|null
decl_stmt|;
DECL|field|fileIdx
specifier|private
name|int
name|fileIdx
init|=
literal|0
decl_stmt|;
DECL|field|cookie
specifier|private
name|String
name|cookie
init|=
literal|null
decl_stmt|;
DECL|field|nextPath
specifier|private
name|Path
name|nextPath
init|=
literal|null
decl_stmt|;
DECL|field|callsMade
specifier|private
name|int
name|callsMade
init|=
literal|0
decl_stmt|;
DECL|method|CorruptFileBlockIterator (DFSClient dfs, Path path)
specifier|public
name|CorruptFileBlockIterator
parameter_list|(
name|DFSClient
name|dfs
parameter_list|,
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|dfs
operator|=
name|dfs
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path2String
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|loadNext
argument_list|()
expr_stmt|;
block|}
comment|/**    * @return the number of calls made to the DFSClient.    * This is for debugging and testing purposes.    */
DECL|method|getCallsMade ()
specifier|public
name|int
name|getCallsMade
parameter_list|()
block|{
return|return
name|callsMade
return|;
block|}
DECL|method|path2String (Path path)
specifier|private
name|String
name|path2String
parameter_list|(
name|Path
name|path
parameter_list|)
block|{
return|return
name|path
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
return|;
block|}
DECL|method|string2Path (String string)
specifier|private
name|Path
name|string2Path
parameter_list|(
name|String
name|string
parameter_list|)
block|{
return|return
operator|new
name|Path
argument_list|(
name|string
argument_list|)
return|;
block|}
DECL|method|loadNext ()
specifier|private
name|void
name|loadNext
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|files
operator|==
literal|null
operator|||
name|fileIdx
operator|>=
name|files
operator|.
name|length
condition|)
block|{
name|CorruptFileBlocks
name|cfb
init|=
name|dfs
operator|.
name|listCorruptFileBlocks
argument_list|(
name|path
argument_list|,
name|cookie
argument_list|)
decl_stmt|;
name|files
operator|=
name|cfb
operator|.
name|getFiles
argument_list|()
expr_stmt|;
name|cookie
operator|=
name|cfb
operator|.
name|getCookie
argument_list|()
expr_stmt|;
name|fileIdx
operator|=
literal|0
expr_stmt|;
name|callsMade
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|fileIdx
operator|>=
name|files
operator|.
name|length
condition|)
block|{
comment|// received an empty response
comment|// there are no more corrupt file blocks
name|nextPath
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|nextPath
operator|=
name|string2Path
argument_list|(
name|files
index|[
name|fileIdx
index|]
argument_list|)
expr_stmt|;
name|fileIdx
operator|++
expr_stmt|;
block|}
block|}
comment|/**    * {@inheritDoc}    */
annotation|@
name|Override
DECL|method|hasNext ()
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|nextPath
operator|!=
literal|null
return|;
block|}
comment|/**    * {@inheritDoc}    */
annotation|@
name|Override
DECL|method|next ()
specifier|public
name|Path
name|next
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|hasNext
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|(
literal|"No more corrupt file blocks"
argument_list|)
throw|;
block|}
name|Path
name|result
init|=
name|nextPath
decl_stmt|;
name|loadNext
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

