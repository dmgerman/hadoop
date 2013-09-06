begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.http
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|http
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|spi
operator|.
name|LoggingEvent
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|AppenderSkeleton
import|;
end_import

begin_comment
comment|/**  * Log4j Appender adapter for HttpRequestLog  */
end_comment

begin_class
DECL|class|HttpRequestLogAppender
specifier|public
class|class
name|HttpRequestLogAppender
extends|extends
name|AppenderSkeleton
block|{
DECL|field|filename
specifier|private
name|String
name|filename
decl_stmt|;
DECL|field|retainDays
specifier|private
name|int
name|retainDays
decl_stmt|;
DECL|method|HttpRequestLogAppender ()
specifier|public
name|HttpRequestLogAppender
parameter_list|()
block|{   }
DECL|method|setRetainDays (int retainDays)
specifier|public
name|void
name|setRetainDays
parameter_list|(
name|int
name|retainDays
parameter_list|)
block|{
name|this
operator|.
name|retainDays
operator|=
name|retainDays
expr_stmt|;
block|}
DECL|method|getRetainDays ()
specifier|public
name|int
name|getRetainDays
parameter_list|()
block|{
return|return
name|retainDays
return|;
block|}
DECL|method|setFilename (String filename)
specifier|public
name|void
name|setFilename
parameter_list|(
name|String
name|filename
parameter_list|)
block|{
name|this
operator|.
name|filename
operator|=
name|filename
expr_stmt|;
block|}
DECL|method|getFilename ()
specifier|public
name|String
name|getFilename
parameter_list|()
block|{
return|return
name|filename
return|;
block|}
annotation|@
name|Override
DECL|method|append (LoggingEvent event)
specifier|public
name|void
name|append
parameter_list|(
name|LoggingEvent
name|event
parameter_list|)
block|{   }
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
block|{   }
annotation|@
name|Override
DECL|method|requiresLayout ()
specifier|public
name|boolean
name|requiresLayout
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

