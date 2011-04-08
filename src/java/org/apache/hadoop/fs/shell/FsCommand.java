begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.shell
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceAudience
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
name|classification
operator|.
name|InterfaceStability
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
name|Path
import|;
end_import

begin_comment
comment|/**  * Base class for all "hadoop fs" commands  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
comment|// this class may not look useful now, but it's a placeholder for future
comment|// functionality to act as a registry for fs commands.  currently it's being
comment|// used to implement unnecessary abstract methods in the base class
DECL|class|FsCommand
specifier|abstract
specifier|public
class|class
name|FsCommand
extends|extends
name|Command
block|{
DECL|method|FsCommand ()
specifier|protected
name|FsCommand
parameter_list|()
block|{}
DECL|method|FsCommand (Configuration conf)
specifier|protected
name|FsCommand
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|super
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
DECL|method|getCommandName ()
specifier|public
name|String
name|getCommandName
parameter_list|()
block|{
return|return
name|name
operator|.
name|startsWith
argument_list|(
literal|"-"
argument_list|)
condition|?
name|name
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
else|:
name|name
return|;
block|}
comment|// abstract method that normally is invoked by runall() which is
comment|// overridden below
DECL|method|run (Path path)
specifier|protected
name|void
name|run
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"not supposed to get here"
argument_list|)
throw|;
block|}
comment|/** @deprecated use {@link #run(String...argv)} */
annotation|@
name|Deprecated
annotation|@
name|Override
DECL|method|runAll ()
specifier|public
name|int
name|runAll
parameter_list|()
block|{
return|return
name|run
argument_list|(
name|args
argument_list|)
return|;
block|}
block|}
end_class

end_unit

