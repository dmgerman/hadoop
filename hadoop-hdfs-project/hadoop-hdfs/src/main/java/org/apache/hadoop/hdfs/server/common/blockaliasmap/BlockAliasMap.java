begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.common.blockaliasmap
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
name|common
operator|.
name|blockaliasmap
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Optional
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
name|Block
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
name|common
operator|.
name|BlockAlias
import|;
end_import

begin_comment
comment|/**  * An abstract class used to read and write block maps for provided blocks.  */
end_comment

begin_class
DECL|class|BlockAliasMap
specifier|public
specifier|abstract
class|class
name|BlockAliasMap
parameter_list|<
name|T
extends|extends
name|BlockAlias
parameter_list|>
block|{
comment|/**    * ImmutableIterator is an Iterator that does not support the remove    * operation. This could inherit {@link java.util.Enumeration} but Iterator    * is supported by more APIs and Enumeration's javadoc even suggests using    * Iterator instead.    */
DECL|class|ImmutableIterator
specifier|public
specifier|abstract
class|class
name|ImmutableIterator
implements|implements
name|Iterator
argument_list|<
name|T
argument_list|>
block|{
DECL|method|remove ()
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Remove is not supported for provided storage"
argument_list|)
throw|;
block|}
block|}
comment|/**    * An abstract class that is used to read {@link BlockAlias}es    * for provided blocks.    */
DECL|class|Reader
specifier|public
specifier|static
specifier|abstract
class|class
name|Reader
parameter_list|<
name|U
extends|extends
name|BlockAlias
parameter_list|>
implements|implements
name|Iterable
argument_list|<
name|U
argument_list|>
implements|,
name|Closeable
block|{
comment|/**      * reader options.      */
DECL|interface|Options
specifier|public
interface|interface
name|Options
block|{ }
comment|/**      * @param ident block to resolve      * @return BlockAlias correspoding to the provided block.      * @throws IOException      */
DECL|method|resolve (Block ident)
specifier|public
specifier|abstract
name|Optional
argument_list|<
name|U
argument_list|>
name|resolve
parameter_list|(
name|Block
name|ident
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
comment|/**    * Returns a reader to the alias map.    * @param opts reader options    * @return {@link Reader} to the alias map.    * @throws IOException    */
DECL|method|getReader (Reader.Options opts)
specifier|public
specifier|abstract
name|Reader
argument_list|<
name|T
argument_list|>
name|getReader
parameter_list|(
name|Reader
operator|.
name|Options
name|opts
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * An abstract class used as a writer for the provided block map.    */
DECL|class|Writer
specifier|public
specifier|static
specifier|abstract
class|class
name|Writer
parameter_list|<
name|U
extends|extends
name|BlockAlias
parameter_list|>
implements|implements
name|Closeable
block|{
comment|/**      * writer options.      */
DECL|interface|Options
specifier|public
interface|interface
name|Options
block|{ }
DECL|method|store (U token)
specifier|public
specifier|abstract
name|void
name|store
parameter_list|(
name|U
name|token
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
comment|/**    * Returns the writer for the alias map.    * @param opts writer options.    * @return {@link Writer} to the alias map.    * @throws IOException    */
DECL|method|getWriter (Writer.Options opts)
specifier|public
specifier|abstract
name|Writer
argument_list|<
name|T
argument_list|>
name|getWriter
parameter_list|(
name|Writer
operator|.
name|Options
name|opts
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Refresh the alias map.    * @throws IOException    */
DECL|method|refresh ()
specifier|public
specifier|abstract
name|void
name|refresh
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|close ()
specifier|public
specifier|abstract
name|void
name|close
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

