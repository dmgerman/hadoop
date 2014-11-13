begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.shell.find
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|shell
operator|.
name|find
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
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
name|util
operator|.
name|Date
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
name|shell
operator|.
name|CommandFactory
import|;
end_import

begin_comment
comment|/**  * Options to be used by the {@link Find} command and its {@link Expression}s.  */
end_comment

begin_class
DECL|class|FindOptions
specifier|public
class|class
name|FindOptions
block|{
comment|/** Output stream to be used. */
DECL|field|out
specifier|private
name|PrintStream
name|out
decl_stmt|;
comment|/** Error stream to be used. */
DECL|field|err
specifier|private
name|PrintStream
name|err
decl_stmt|;
comment|/** Input stream to be used. */
DECL|field|in
specifier|private
name|InputStream
name|in
decl_stmt|;
comment|/**    * Indicates whether the expression should be applied to the directory tree    * depth first.    */
DECL|field|depthFirst
specifier|private
name|boolean
name|depthFirst
init|=
literal|false
decl_stmt|;
comment|/** Indicates whether symbolic links should be followed. */
DECL|field|followLink
specifier|private
name|boolean
name|followLink
init|=
literal|false
decl_stmt|;
comment|/**    * Indicates whether symbolic links specified as command arguments should be    * followed.    */
DECL|field|followArgLink
specifier|private
name|boolean
name|followArgLink
init|=
literal|false
decl_stmt|;
comment|/** Start time of the find process. */
DECL|field|startTime
specifier|private
name|long
name|startTime
init|=
operator|new
name|Date
argument_list|()
operator|.
name|getTime
argument_list|()
decl_stmt|;
comment|/**    * Depth at which to start applying expressions.    */
DECL|field|minDepth
specifier|private
name|int
name|minDepth
init|=
literal|0
decl_stmt|;
comment|/**    * Depth at which to stop applying expressions.    */
DECL|field|maxDepth
specifier|private
name|int
name|maxDepth
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
comment|/** Factory for retrieving command classes. */
DECL|field|commandFactory
specifier|private
name|CommandFactory
name|commandFactory
decl_stmt|;
comment|/** Configuration object. */
DECL|field|configuration
specifier|private
name|Configuration
name|configuration
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
comment|/**    * Sets the output stream to be used.    *    * @param out output stream to be used    */
DECL|method|setOut (PrintStream out)
specifier|public
name|void
name|setOut
parameter_list|(
name|PrintStream
name|out
parameter_list|)
block|{
name|this
operator|.
name|out
operator|=
name|out
expr_stmt|;
block|}
comment|/**    * Returns the output stream to be used.    *    * @return output stream to be used    */
DECL|method|getOut ()
specifier|public
name|PrintStream
name|getOut
parameter_list|()
block|{
return|return
name|this
operator|.
name|out
return|;
block|}
comment|/**    * Sets the error stream to be used.    *    * @param err error stream to be used    */
DECL|method|setErr (PrintStream err)
specifier|public
name|void
name|setErr
parameter_list|(
name|PrintStream
name|err
parameter_list|)
block|{
name|this
operator|.
name|err
operator|=
name|err
expr_stmt|;
block|}
comment|/**    * Returns the error stream to be used.    *    * @return error stream to be used    */
DECL|method|getErr ()
specifier|public
name|PrintStream
name|getErr
parameter_list|()
block|{
return|return
name|this
operator|.
name|err
return|;
block|}
comment|/**    * Sets the input stream to be used.    *    * @param in input stream to be used    */
DECL|method|setIn (InputStream in)
specifier|public
name|void
name|setIn
parameter_list|(
name|InputStream
name|in
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
block|}
comment|/**    * Returns the input stream to be used.    *    * @return input stream to be used    */
DECL|method|getIn ()
specifier|public
name|InputStream
name|getIn
parameter_list|()
block|{
return|return
name|this
operator|.
name|in
return|;
block|}
comment|/**    * Sets flag indicating whether the expression should be applied to the    * directory tree depth first.    *    * @param depthFirst true indicates depth first traversal    */
DECL|method|setDepthFirst (boolean depthFirst)
specifier|public
name|void
name|setDepthFirst
parameter_list|(
name|boolean
name|depthFirst
parameter_list|)
block|{
name|this
operator|.
name|depthFirst
operator|=
name|depthFirst
expr_stmt|;
block|}
comment|/**    * Should directory tree be traversed depth first?    *    * @return true indicate depth first traversal    */
DECL|method|isDepthFirst ()
specifier|public
name|boolean
name|isDepthFirst
parameter_list|()
block|{
return|return
name|this
operator|.
name|depthFirst
return|;
block|}
comment|/**    * Sets flag indicating whether symbolic links should be followed.    *    * @param followLink true indicates follow links    */
DECL|method|setFollowLink (boolean followLink)
specifier|public
name|void
name|setFollowLink
parameter_list|(
name|boolean
name|followLink
parameter_list|)
block|{
name|this
operator|.
name|followLink
operator|=
name|followLink
expr_stmt|;
block|}
comment|/**    * Should symbolic links be follows?    *    * @return true indicates links should be followed    */
DECL|method|isFollowLink ()
specifier|public
name|boolean
name|isFollowLink
parameter_list|()
block|{
return|return
name|this
operator|.
name|followLink
return|;
block|}
comment|/**    * Sets flag indicating whether command line symbolic links should be    * followed.    *    * @param followArgLink true indicates follow links    */
DECL|method|setFollowArgLink (boolean followArgLink)
specifier|public
name|void
name|setFollowArgLink
parameter_list|(
name|boolean
name|followArgLink
parameter_list|)
block|{
name|this
operator|.
name|followArgLink
operator|=
name|followArgLink
expr_stmt|;
block|}
comment|/**    * Should command line symbolic links be follows?    *    * @return true indicates links should be followed    */
DECL|method|isFollowArgLink ()
specifier|public
name|boolean
name|isFollowArgLink
parameter_list|()
block|{
return|return
name|this
operator|.
name|followArgLink
return|;
block|}
comment|/**    * Returns the start time of this {@link Find} command.    *    * @return start time (in milliseconds since epoch)    */
DECL|method|getStartTime ()
specifier|public
name|long
name|getStartTime
parameter_list|()
block|{
return|return
name|this
operator|.
name|startTime
return|;
block|}
comment|/**    * Set the start time of this {@link Find} command.    *    * @param time start time (in milliseconds since epoch)    */
DECL|method|setStartTime (long time)
specifier|public
name|void
name|setStartTime
parameter_list|(
name|long
name|time
parameter_list|)
block|{
name|this
operator|.
name|startTime
operator|=
name|time
expr_stmt|;
block|}
comment|/**    * Returns the minimum depth for applying expressions.    *    * @return min depth    */
DECL|method|getMinDepth ()
specifier|public
name|int
name|getMinDepth
parameter_list|()
block|{
return|return
name|this
operator|.
name|minDepth
return|;
block|}
comment|/**    * Sets the minimum depth for applying expressions.    *    * @param minDepth minimum depth    */
DECL|method|setMinDepth (int minDepth)
specifier|public
name|void
name|setMinDepth
parameter_list|(
name|int
name|minDepth
parameter_list|)
block|{
name|this
operator|.
name|minDepth
operator|=
name|minDepth
expr_stmt|;
block|}
comment|/**    * Returns the maximum depth for applying expressions.    *    * @return maximum depth    */
DECL|method|getMaxDepth ()
specifier|public
name|int
name|getMaxDepth
parameter_list|()
block|{
return|return
name|this
operator|.
name|maxDepth
return|;
block|}
comment|/**    * Sets the maximum depth for applying expressions.    *    * @param maxDepth maximum depth    */
DECL|method|setMaxDepth (int maxDepth)
specifier|public
name|void
name|setMaxDepth
parameter_list|(
name|int
name|maxDepth
parameter_list|)
block|{
name|this
operator|.
name|maxDepth
operator|=
name|maxDepth
expr_stmt|;
block|}
comment|/**    * Set the command factory.    *    * @param factory {@link CommandFactory}    */
DECL|method|setCommandFactory (CommandFactory factory)
specifier|public
name|void
name|setCommandFactory
parameter_list|(
name|CommandFactory
name|factory
parameter_list|)
block|{
name|this
operator|.
name|commandFactory
operator|=
name|factory
expr_stmt|;
block|}
comment|/**    * Return the command factory.    *    * @return {@link CommandFactory}    */
DECL|method|getCommandFactory ()
specifier|public
name|CommandFactory
name|getCommandFactory
parameter_list|()
block|{
return|return
name|this
operator|.
name|commandFactory
return|;
block|}
comment|/**    * Set the {@link Configuration}    *    * @param configuration {@link Configuration}    */
DECL|method|setConfiguration (Configuration configuration)
specifier|public
name|void
name|setConfiguration
parameter_list|(
name|Configuration
name|configuration
parameter_list|)
block|{
name|this
operator|.
name|configuration
operator|=
name|configuration
expr_stmt|;
block|}
comment|/**    * Return the {@link Configuration} return configuration {@link Configuration}    */
DECL|method|getConfiguration ()
specifier|public
name|Configuration
name|getConfiguration
parameter_list|()
block|{
return|return
name|this
operator|.
name|configuration
return|;
block|}
block|}
end_class

end_unit

