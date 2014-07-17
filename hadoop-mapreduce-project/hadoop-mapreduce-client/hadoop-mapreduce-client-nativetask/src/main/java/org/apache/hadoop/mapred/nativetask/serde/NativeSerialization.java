begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred.nativetask.serde
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|nativetask
operator|.
name|serde
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
name|concurrent
operator|.
name|ConcurrentHashMap
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
name|io
operator|.
name|Writable
import|;
end_import

begin_class
DECL|class|NativeSerialization
specifier|public
class|class
name|NativeSerialization
block|{
DECL|field|map
specifier|private
specifier|final
name|ConcurrentHashMap
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
name|map
init|=
operator|new
name|ConcurrentHashMap
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
DECL|method|accept (Class<?> c)
specifier|public
name|boolean
name|accept
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|c
parameter_list|)
block|{
return|return
name|Writable
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|c
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|getSerializer (Class<?> c)
specifier|public
name|INativeSerializer
argument_list|<
name|Writable
argument_list|>
name|getSerializer
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|c
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
literal|null
operator|==
name|c
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
operator|!
name|Writable
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|c
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Cannot serialize type "
operator|+
name|c
operator|.
name|getName
argument_list|()
operator|+
literal|", we only accept subclass of Writable"
argument_list|)
throw|;
block|}
specifier|final
name|String
name|name
init|=
name|c
operator|.
name|getName
argument_list|()
decl_stmt|;
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|serializer
init|=
name|map
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|serializer
condition|)
block|{
try|try
block|{
return|return
operator|(
name|INativeSerializer
argument_list|<
name|Writable
argument_list|>
operator|)
name|serializer
operator|.
name|newInstance
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
return|return
operator|new
name|DefaultSerializer
argument_list|()
return|;
block|}
DECL|method|register (String klass, Class<?> serializer)
specifier|public
name|void
name|register
parameter_list|(
name|String
name|klass
parameter_list|,
name|Class
argument_list|<
name|?
argument_list|>
name|serializer
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
literal|null
operator|==
name|klass
operator|||
literal|null
operator|==
name|serializer
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"invalid arguments, klass or serializer is null"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|INativeSerializer
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|serializer
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Serializer is not assigable from INativeSerializer"
argument_list|)
throw|;
block|}
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|storedSerializer
init|=
name|map
operator|.
name|get
argument_list|(
name|klass
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|storedSerializer
condition|)
block|{
name|map
operator|.
name|put
argument_list|(
name|klass
argument_list|,
name|serializer
argument_list|)
expr_stmt|;
return|return;
block|}
else|else
block|{
if|if
condition|(
operator|!
name|storedSerializer
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|serializer
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Error! Serializer already registered, exist: "
operator|+
name|storedSerializer
operator|.
name|getName
argument_list|()
operator|+
literal|", new: "
operator|+
name|serializer
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|reset ()
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|map
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
DECL|field|instance
specifier|private
specifier|static
name|NativeSerialization
name|instance
init|=
operator|new
name|NativeSerialization
argument_list|()
decl_stmt|;
DECL|method|getInstance ()
specifier|public
specifier|static
name|NativeSerialization
name|getInstance
parameter_list|()
block|{
return|return
name|instance
return|;
block|}
block|}
end_class

end_unit

