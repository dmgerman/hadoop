begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a.s3guard
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3a
operator|.
name|s3guard
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
name|Iterator
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
name|s3a
operator|.
name|S3AFileStatus
import|;
end_import

begin_comment
comment|/**  * Internal iterators.  */
end_comment

begin_class
DECL|class|InternalIterators
specifier|final
class|class
name|InternalIterators
block|{
DECL|method|InternalIterators ()
specifier|private
name|InternalIterators
parameter_list|()
block|{   }
comment|/**    * From a remote status iterator, build a path iterator.    */
DECL|class|PathFromRemoteStatusIterator
specifier|static
specifier|final
class|class
name|PathFromRemoteStatusIterator
implements|implements
name|RemoteIterator
argument_list|<
name|Path
argument_list|>
block|{
DECL|field|source
specifier|private
specifier|final
name|RemoteIterator
argument_list|<
name|S3AFileStatus
argument_list|>
name|source
decl_stmt|;
comment|/**      * Construct.      * @param source source iterator.      */
DECL|method|PathFromRemoteStatusIterator (final RemoteIterator<S3AFileStatus> source)
name|PathFromRemoteStatusIterator
parameter_list|(
specifier|final
name|RemoteIterator
argument_list|<
name|S3AFileStatus
argument_list|>
name|source
parameter_list|)
block|{
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hasNext ()
specifier|public
name|boolean
name|hasNext
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|source
operator|.
name|hasNext
argument_list|()
return|;
block|}
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
return|return
name|source
operator|.
name|next
argument_list|()
operator|.
name|getPath
argument_list|()
return|;
block|}
block|}
comment|/**    * From a classic java.util.Iterator, build a Hadoop remote iterator.    * @param<T> type of iterated value.    */
DECL|class|RemoteIteratorFromIterator
specifier|static
specifier|final
class|class
name|RemoteIteratorFromIterator
parameter_list|<
name|T
parameter_list|>
implements|implements
name|RemoteIterator
argument_list|<
name|T
argument_list|>
block|{
DECL|field|source
specifier|private
specifier|final
name|Iterator
argument_list|<
name|T
argument_list|>
name|source
decl_stmt|;
comment|/**      * Construct.      * @param source source iterator.      */
DECL|method|RemoteIteratorFromIterator (final Iterator<T> source)
name|RemoteIteratorFromIterator
parameter_list|(
specifier|final
name|Iterator
argument_list|<
name|T
argument_list|>
name|source
parameter_list|)
block|{
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hasNext ()
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|source
operator|.
name|hasNext
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|next ()
specifier|public
name|T
name|next
parameter_list|()
block|{
return|return
name|source
operator|.
name|next
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

