begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ipc
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ipc
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
name|lang
operator|.
name|reflect
operator|.
name|Constructor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|Attributes
import|;
end_import

begin_import
import|import
name|org
operator|.
name|znerd
operator|.
name|xmlenc
operator|.
name|XMLOutputter
import|;
end_import

begin_class
DECL|class|RemoteException
specifier|public
class|class
name|RemoteException
extends|extends
name|IOException
block|{
comment|/** For java.io.Serializable */
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
DECL|field|className
specifier|private
name|String
name|className
decl_stmt|;
DECL|method|RemoteException (String className, String msg)
specifier|public
name|RemoteException
parameter_list|(
name|String
name|className
parameter_list|,
name|String
name|msg
parameter_list|)
block|{
name|super
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|this
operator|.
name|className
operator|=
name|className
expr_stmt|;
block|}
DECL|method|getClassName ()
specifier|public
name|String
name|getClassName
parameter_list|()
block|{
return|return
name|className
return|;
block|}
comment|/**    * If this remote exception wraps up one of the lookupTypes    * then return this exception.    *<p>    * Unwraps any IOException.    *     * @param lookupTypes the desired exception class.    * @return IOException, which is either the lookupClass exception or this.    */
DECL|method|unwrapRemoteException (Class<?>.... lookupTypes)
specifier|public
name|IOException
name|unwrapRemoteException
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
modifier|...
name|lookupTypes
parameter_list|)
block|{
if|if
condition|(
name|lookupTypes
operator|==
literal|null
condition|)
return|return
name|this
return|;
for|for
control|(
name|Class
argument_list|<
name|?
argument_list|>
name|lookupClass
range|:
name|lookupTypes
control|)
block|{
if|if
condition|(
operator|!
name|lookupClass
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|getClassName
argument_list|()
argument_list|)
condition|)
continue|continue;
try|try
block|{
return|return
name|instantiateException
argument_list|(
name|lookupClass
operator|.
name|asSubclass
argument_list|(
name|IOException
operator|.
name|class
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// cannot instantiate lookupClass, just return this
return|return
name|this
return|;
block|}
block|}
comment|// wrapped up exception is not in lookupTypes, just return this
return|return
name|this
return|;
block|}
comment|/**    * Instantiate and return the exception wrapped up by this remote exception.    *     *<p> This unwraps any<code>Throwable</code> that has a constructor taking    * a<code>String</code> as a parameter.    * Otherwise it returns this.    *     * @return<code>Throwable    */
DECL|method|unwrapRemoteException ()
specifier|public
name|IOException
name|unwrapRemoteException
parameter_list|()
block|{
try|try
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|realClass
init|=
name|Class
operator|.
name|forName
argument_list|(
name|getClassName
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|instantiateException
argument_list|(
name|realClass
operator|.
name|asSubclass
argument_list|(
name|IOException
operator|.
name|class
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// cannot instantiate the original exception, just return this
block|}
return|return
name|this
return|;
block|}
DECL|method|instantiateException (Class<? extends IOException> cls)
specifier|private
name|IOException
name|instantiateException
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|IOException
argument_list|>
name|cls
parameter_list|)
throws|throws
name|Exception
block|{
name|Constructor
argument_list|<
name|?
extends|extends
name|IOException
argument_list|>
name|cn
init|=
name|cls
operator|.
name|getConstructor
argument_list|(
name|String
operator|.
name|class
argument_list|)
decl_stmt|;
name|cn
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|String
name|firstLine
init|=
name|this
operator|.
name|getMessage
argument_list|()
decl_stmt|;
name|int
name|eol
init|=
name|firstLine
operator|.
name|indexOf
argument_list|(
literal|'\n'
argument_list|)
decl_stmt|;
if|if
condition|(
name|eol
operator|>=
literal|0
condition|)
block|{
name|firstLine
operator|=
name|firstLine
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|eol
argument_list|)
expr_stmt|;
block|}
name|IOException
name|ex
init|=
name|cn
operator|.
name|newInstance
argument_list|(
name|firstLine
argument_list|)
decl_stmt|;
name|ex
operator|.
name|initCause
argument_list|(
name|this
argument_list|)
expr_stmt|;
return|return
name|ex
return|;
block|}
comment|/** Write the object to XML format */
DECL|method|writeXml (String path, XMLOutputter doc)
specifier|public
name|void
name|writeXml
parameter_list|(
name|String
name|path
parameter_list|,
name|XMLOutputter
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|doc
operator|.
name|startTag
argument_list|(
name|RemoteException
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
name|doc
operator|.
name|attribute
argument_list|(
literal|"path"
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|doc
operator|.
name|attribute
argument_list|(
literal|"class"
argument_list|,
name|getClassName
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|msg
init|=
name|getLocalizedMessage
argument_list|()
decl_stmt|;
name|int
name|i
init|=
name|msg
operator|.
name|indexOf
argument_list|(
literal|"\n"
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|>=
literal|0
condition|)
block|{
name|msg
operator|=
name|msg
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
name|doc
operator|.
name|attribute
argument_list|(
literal|"message"
argument_list|,
name|msg
operator|.
name|substring
argument_list|(
name|msg
operator|.
name|indexOf
argument_list|(
literal|":"
argument_list|)
operator|+
literal|1
argument_list|)
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
name|doc
operator|.
name|endTag
argument_list|()
expr_stmt|;
block|}
comment|/** Create RemoteException from attributes */
DECL|method|valueOf (Attributes attrs)
specifier|public
specifier|static
name|RemoteException
name|valueOf
parameter_list|(
name|Attributes
name|attrs
parameter_list|)
block|{
return|return
operator|new
name|RemoteException
argument_list|(
name|attrs
operator|.
name|getValue
argument_list|(
literal|"class"
argument_list|)
argument_list|,
name|attrs
operator|.
name|getValue
argument_list|(
literal|"message"
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

