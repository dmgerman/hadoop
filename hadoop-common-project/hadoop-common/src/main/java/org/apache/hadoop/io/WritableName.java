begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.io
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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

begin_comment
comment|/** Utility to permit renaming of Writable implementation classes without  * invalidiating files that contain their class name.  */
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
DECL|class|WritableName
specifier|public
class|class
name|WritableName
block|{
DECL|field|NAME_TO_CLASS
specifier|private
specifier|static
name|HashMap
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
name|NAME_TO_CLASS
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|CLASS_TO_NAME
specifier|private
specifier|static
name|HashMap
argument_list|<
name|Class
argument_list|<
name|?
argument_list|>
argument_list|,
name|String
argument_list|>
name|CLASS_TO_NAME
init|=
operator|new
name|HashMap
argument_list|<
name|Class
argument_list|<
name|?
argument_list|>
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
static|static
block|{
comment|// define important types
name|WritableName
operator|.
name|setName
argument_list|(
name|NullWritable
operator|.
name|class
argument_list|,
literal|"null"
argument_list|)
expr_stmt|;
name|WritableName
operator|.
name|setName
argument_list|(
name|LongWritable
operator|.
name|class
argument_list|,
literal|"long"
argument_list|)
expr_stmt|;
name|WritableName
operator|.
name|setName
argument_list|(
name|UTF8
operator|.
name|class
argument_list|,
literal|"UTF8"
argument_list|)
expr_stmt|;
name|WritableName
operator|.
name|setName
argument_list|(
name|MD5Hash
operator|.
name|class
argument_list|,
literal|"MD5Hash"
argument_list|)
expr_stmt|;
block|}
DECL|method|WritableName ()
specifier|private
name|WritableName
parameter_list|()
block|{}
comment|// no public ctor
comment|/** Set the name that a class should be known as to something other than the    * class name. */
DECL|method|setName (Class<?> writableClass, String name)
specifier|public
specifier|static
specifier|synchronized
name|void
name|setName
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|writableClass
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|CLASS_TO_NAME
operator|.
name|put
argument_list|(
name|writableClass
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|NAME_TO_CLASS
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|writableClass
argument_list|)
expr_stmt|;
block|}
comment|/** Add an alternate name for a class. */
DECL|method|addName (Class<?> writableClass, String name)
specifier|public
specifier|static
specifier|synchronized
name|void
name|addName
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|writableClass
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|NAME_TO_CLASS
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|writableClass
argument_list|)
expr_stmt|;
block|}
comment|/** Return the name for a class.  Default is {@link Class#getName()}. */
DECL|method|getName (Class<?> writableClass)
specifier|public
specifier|static
specifier|synchronized
name|String
name|getName
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|writableClass
parameter_list|)
block|{
name|String
name|name
init|=
name|CLASS_TO_NAME
operator|.
name|get
argument_list|(
name|writableClass
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|!=
literal|null
condition|)
return|return
name|name
return|;
return|return
name|writableClass
operator|.
name|getName
argument_list|()
return|;
block|}
comment|/** Return the class for a name.  Default is {@link Class#forName(String)}.*/
DECL|method|getClass (String name, Configuration conf )
specifier|public
specifier|static
specifier|synchronized
name|Class
argument_list|<
name|?
argument_list|>
name|getClass
parameter_list|(
name|String
name|name
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|writableClass
init|=
name|NAME_TO_CLASS
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|writableClass
operator|!=
literal|null
condition|)
return|return
name|writableClass
operator|.
name|asSubclass
argument_list|(
name|Writable
operator|.
name|class
argument_list|)
return|;
try|try
block|{
return|return
name|conf
operator|.
name|getClassByName
argument_list|(
name|name
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
name|IOException
name|newE
init|=
operator|new
name|IOException
argument_list|(
literal|"WritableName can't load class: "
operator|+
name|name
argument_list|)
decl_stmt|;
name|newE
operator|.
name|initCause
argument_list|(
name|e
argument_list|)
expr_stmt|;
throw|throw
name|newE
throw|;
block|}
block|}
block|}
end_class

end_unit

