begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.tools
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|tools
package|;
end_package

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
name|security
operator|.
name|Credentials
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_comment
comment|/**  * GlobbedCopyListing implements the CopyListing interface, to create the copy  * listing-file by "globbing" all specified source paths (wild-cards and all.)  */
end_comment

begin_class
DECL|class|GlobbedCopyListing
specifier|public
class|class
name|GlobbedCopyListing
extends|extends
name|CopyListing
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
name|GlobbedCopyListing
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|simpleListing
specifier|private
specifier|final
name|CopyListing
name|simpleListing
decl_stmt|;
comment|/**    * Constructor, to initialize the configuration.    * @param configuration The input Configuration object.    * @param credentials Credentials object on which the FS delegation tokens are cached. If null    * delegation token caching is skipped    */
DECL|method|GlobbedCopyListing (Configuration configuration, Credentials credentials)
specifier|public
name|GlobbedCopyListing
parameter_list|(
name|Configuration
name|configuration
parameter_list|,
name|Credentials
name|credentials
parameter_list|)
block|{
name|super
argument_list|(
name|configuration
argument_list|,
name|credentials
argument_list|)
expr_stmt|;
name|simpleListing
operator|=
operator|new
name|SimpleCopyListing
argument_list|(
name|getConf
argument_list|()
argument_list|,
name|credentials
argument_list|)
expr_stmt|;
block|}
comment|/** {@inheritDoc} */
annotation|@
name|Override
DECL|method|validatePaths (DistCpContext context)
specifier|protected
name|void
name|validatePaths
parameter_list|(
name|DistCpContext
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|InvalidInputException
block|{   }
comment|/**    * Implementation of CopyListing::buildListing().    * Creates the copy listing by "globbing" all source-paths.    * @param pathToListingFile The location at which the copy-listing file    *                           is to be created.    * @param context The distcp context with associated input options.    * @throws IOException    */
annotation|@
name|Override
DECL|method|doBuildListing (Path pathToListingFile, DistCpContext context)
specifier|public
name|void
name|doBuildListing
parameter_list|(
name|Path
name|pathToListingFile
parameter_list|,
name|DistCpContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|Path
argument_list|>
name|globbedPaths
init|=
operator|new
name|ArrayList
argument_list|<
name|Path
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|context
operator|.
name|getSourcePaths
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|InvalidInputException
argument_list|(
literal|"Nothing to process. Source paths::EMPTY"
argument_list|)
throw|;
block|}
for|for
control|(
name|Path
name|p
range|:
name|context
operator|.
name|getSourcePaths
argument_list|()
control|)
block|{
name|FileSystem
name|fs
init|=
name|p
operator|.
name|getFileSystem
argument_list|(
name|getConf
argument_list|()
argument_list|)
decl_stmt|;
name|FileStatus
index|[]
name|inputs
init|=
name|fs
operator|.
name|globStatus
argument_list|(
name|p
argument_list|)
decl_stmt|;
if|if
condition|(
name|inputs
operator|!=
literal|null
operator|&&
name|inputs
operator|.
name|length
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|FileStatus
name|onePath
range|:
name|inputs
control|)
block|{
name|globbedPaths
operator|.
name|add
argument_list|(
name|onePath
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|InvalidInputException
argument_list|(
name|p
operator|+
literal|" doesn't exist"
argument_list|)
throw|;
block|}
block|}
name|context
operator|.
name|setSourcePaths
argument_list|(
name|globbedPaths
argument_list|)
expr_stmt|;
name|simpleListing
operator|.
name|buildListing
argument_list|(
name|pathToListingFile
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
comment|/** {@inheritDoc} */
annotation|@
name|Override
DECL|method|getBytesToCopy ()
specifier|protected
name|long
name|getBytesToCopy
parameter_list|()
block|{
return|return
name|simpleListing
operator|.
name|getBytesToCopy
argument_list|()
return|;
block|}
comment|/** {@inheritDoc} */
annotation|@
name|Override
DECL|method|getNumberOfPaths ()
specifier|protected
name|long
name|getNumberOfPaths
parameter_list|()
block|{
return|return
name|simpleListing
operator|.
name|getNumberOfPaths
argument_list|()
return|;
block|}
block|}
end_class

end_unit

