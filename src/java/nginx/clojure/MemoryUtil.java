package nginx.clojure;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sun.misc.Unsafe;
import clojure.lang.IFn;
import clojure.lang.RT;
import static nginx.clojure.Constants.*;

public class MemoryUtil {


	
	


	public static long[] MEM_INDEX;
	

	
	protected static Unsafe UNSAFE = null;
	
	private static List<IFn>  HANDLERS = new ArrayList<IFn>();
	
	//mapping clojure code pointer address to clojure code id 
	private static Map<Long, Integer> CODE_MAP = new HashMap<>();
	
	
	public native static long ngx_palloc(long pool, long size);
	
	public native static long ngx_pcalloc(long pool, long size);
	
	public native static long ngx_create_temp_buf(long pool, long size);
	
	public native static long ngx_create_file_buf(long r, long file, long name_len);
	
	public native static long ngx_http_send_header(long r);
	
	public native static long ngx_http_output_filter(long r, long chain);

	public native static long ngx_http_clojure_mem_init_ngx_buf(long buf, Object obj, long offset, long len, int last_buf);
	
	public native static long ngx_http_clojure_mem_get_obj_attr(Object obj);
	
	public native static void ngx_http_clojure_mem_copy_to_obj(long src, Object obj, long offset, long len);
	
	public native static void ngx_http_clojure_mem_copy_to_addr(Object obj, long offset, long dest, long len);
	
	public native static long ngx_http_clojure_mem_get_header(long headers_in, long name, long len);
	
	public native static long ngx_http_clojure_mem_get_variable(long r, long name);
	
	public static synchronized void initMemIndex(long idxpt) {
		if (UNSAFE != null) {
			return;
		}
	    try{
	        Field field = Unsafe.class.getDeclaredField("theUnsafe");
	        field.setAccessible(true);
	        UNSAFE = (Unsafe)field.get(null);
	    }
	    catch (Exception e){
	        throw new RuntimeException(e);
	    }
	    
	    BYTE_ARRAY_OFFSET = UNSAFE.arrayBaseOffset(byte[].class);
	    
	    long[] index = new long[NGX_HTTP_CLOJURE_MEM_IDX_END + 1];
	    for (int i = 0; i < NGX_HTTP_CLOJURE_MEM_IDX_END + 1; i++) {
	    	index[i] = UNSAFE.getLong(idxpt + i * 8);
	    }
	    
	    
		MEM_INDEX = index;
		NGX_HTTP_CLOJURE_UINT_SIZE = MEM_INDEX[NGX_HTTP_CLOJURE_UINT_SIZE_IDX];
		
		NGX_HTTP_CLOJURE_STR_SIZE = MEM_INDEX[NGX_HTTP_CLOJURE_STR_SIZE_IDX];
		NGX_HTTP_CLOJURE_STR_LEN_OFFSET = MEM_INDEX[NGX_HTTP_CLOJURE_STR_LEN_IDX];
		NGX_HTTP_CLOJURE_STR_DATA_OFFSET = MEM_INDEX[NGX_HTTP_CLOJURE_STR_DATA_IDX];
		NGX_HTTP_CLOJURE_SIZET_SIZE = MEM_INDEX[NGX_HTTP_CLOJURE_SIZET_SIZE_IDX];
		NGX_HTTP_CLOJURE_OFFT_SIZE = MEM_INDEX[NGX_HTTP_CLOJURE_OFFT_SIZE_IDX];
		
		NGX_HTTP_CLOJURE_TELT_SIZE = MEM_INDEX[NGX_HTTP_CLOJURE_TELT_SIZE_IDX];
		NGX_HTTP_CLOJURE_TELT_HASH_OFFSET = MEM_INDEX[NGX_HTTP_CLOJURE_TELT_HASH_IDX];
		NGX_HTTP_CLOJURE_TELT_KEY_OFFSET = MEM_INDEX[NGX_HTTP_CLOJURE_TELT_KEY_IDX];
		NGX_HTTP_CLOJURE_TELT_VALUE_OFFSET = MEM_INDEX[NGX_HTTP_CLOJURE_TELT_VALUE_IDX];
		NGX_HTTP_CLOJURE_TELT_LOWCASE_KEY_OFFSET = MEM_INDEX[NGX_HTTP_CLOJURE_TELT_LOWCASE_KEY_IDX];
		
		NGX_HTTP_CLOJURE_REQ_SIZE = MEM_INDEX[NGX_HTTP_CLOJURE_REQ_SIZE_IDX];
		NGX_HTTP_CLOJURE_REQ_METHOD_OFFSET = MEM_INDEX[NGX_HTTP_CLOJURE_REQ_METHOD_IDX];
		NGX_HTTP_CLOJURE_REQ_URI_OFFSET = MEM_INDEX[NGX_HTTP_CLOJURE_REQ_URI_IDX];
		NGX_HTTP_CLOJURE_REQ_ARGS_OFFSET = MEM_INDEX[NGX_HTTP_CLOJURE_REQ_ARGS_IDX];
		NGX_HTTP_CLOJURE_REQ_HEADERS_IN_OFFSET = MEM_INDEX[NGX_HTTP_CLOJURE_REQ_HEADERS_IN_IDX];
		NGX_HTTP_CLOJURE_REQ_POOL_OFFSET = MEM_INDEX[NGX_HTTP_CLOJURE_REQ_POOL_IDX];
		NGX_HTTP_CLOJURE_REQ_HEADERS_OUT_OFFSET = MEM_INDEX[NGX_HTTP_CLOJURE_REQ_HEADERS_OUT_IDX];
		
		NGX_HTTP_CLOJURE_CHAIN_SIZE = MEM_INDEX[NGX_HTTP_CLOJURE_CHAIN_SIZE_IDX];
		NGX_HTTP_CLOJURE_CHAIN_BUF_OFFSET = MEM_INDEX[NGX_HTTP_CLOJURE_CHAIN_BUF_IDX];
		NGX_HTTP_CLOJURE_CHAIN_NEXT_OFFSET = MEM_INDEX[NGX_HTTP_CLOJURE_CHAIN_NEXT_IDX];
		
		NGX_HTTP_CLOJURE_VARIABLE_SIZE = MEM_INDEX[NGX_HTTP_CLOJURE_VARIABLE_SIZE_IDX];
		NGX_HTTP_CLOJURE_CORE_VARIABLES_ADDR = MEM_INDEX[NGX_HTTP_CLOJURE_CORE_VARIABLES_ADDR_IDX];
		NGX_HTTP_CLOJURE_CORE_VARIABLES_LEN = MEM_INDEX[NGX_HTTP_CLOJURE_CORE_VARIABLES_LEN_IDX];
		
		NGX_HTTP_CLOJURE_HEADERSI_SIZE =  MEM_INDEX[NGX_HTTP_CLOJURE_HEADERSI_SIZE_IDX];
		NGX_HTTP_CLOJURE_HEADERSI_HOST_OFFSET = MEM_INDEX[NGX_HTTP_CLOJURE_HEADERSI_HOST_IDX];
		NGX_HTTP_CLOJURE_HEADERSI_CONNECTION_OFFSET = MEM_INDEX[NGX_HTTP_CLOJURE_HEADERSI_CONNECTION_IDX];
		NGX_HTTP_CLOJURE_HEADERSI_IF_MODIFIED_SINCE_OFFSET = MEM_INDEX[NGX_HTTP_CLOJURE_HEADERSI_IF_MODIFIED_SINCE_IDX];
		NGX_HTTP_CLOJURE_HEADERSI_IF_UNMODIFIED_SINCE_OFFSET = MEM_INDEX[NGX_HTTP_CLOJURE_HEADERSI_IF_UNMODIFIED_SINCE_IDX];
		NGX_HTTP_CLOJURE_HEADERSI_USER_AGENT_OFFSET = MEM_INDEX[NGX_HTTP_CLOJURE_HEADERSI_USER_AGENT_IDX];
		NGX_HTTP_CLOJURE_HEADERSI_REFERER_OFFSET = MEM_INDEX[NGX_HTTP_CLOJURE_HEADERSI_REFERER_IDX];
		NGX_HTTP_CLOJURE_HEADERSI_CONTENT_LENGTH_OFFSET = MEM_INDEX[NGX_HTTP_CLOJURE_HEADERSI_CONTENT_LENGTH_IDX];
		NGX_HTTP_CLOJURE_HEADERSI_CONTENT_TYPE_OFFSET = MEM_INDEX[NGX_HTTP_CLOJURE_HEADERSI_CONTENT_TYPE_IDX];
		NGX_HTTP_CLOJURE_HEADERSI_RANGE_OFFSET = MEM_INDEX[NGX_HTTP_CLOJURE_HEADERSI_RANGE_IDX];
		NGX_HTTP_CLOJURE_HEADERSI_IF_RANGE_OFFSET = MEM_INDEX[NGX_HTTP_CLOJURE_HEADERSI_IF_RANGE_IDX];
		NGX_HTTP_CLOJURE_HEADERSI_TRANSFER_ENCODING_OFFSET = MEM_INDEX[NGX_HTTP_CLOJURE_HEADERSI_TRANSFER_ENCODING_IDX];
		NGX_HTTP_CLOJURE_HEADERSI_EXPECT_OFFSET = MEM_INDEX[NGX_HTTP_CLOJURE_HEADERSI_EXPECT_IDX];

		//#if (NGX_HTTP_GZIP)
		NGX_HTTP_CLOJURE_HEADERSI_ACCEPT_ENCODING_OFFSET = MEM_INDEX[NGX_HTTP_CLOJURE_HEADERSI_ACCEPT_ENCODING_IDX];
		NGX_HTTP_CLOJURE_HEADERSI_VIA_OFFSET = MEM_INDEX[NGX_HTTP_CLOJURE_HEADERSI_VIA_IDX];
		//#endif

		MEM_INDEX[NGX_HTTP_CLOJURE_HEADERSI_AUTHORIZATION_IDX] =  NGX_HTTP_CLOJURE_HEADERSI_AUTHORIZATION_OFFSET;
		MEM_INDEX[NGX_HTTP_CLOJURE_HEADERSI_KEEP_ALIVE_IDX] =  NGX_HTTP_CLOJURE_HEADERSI_KEEP_ALIVE_OFFSET;

		//#if (NGX_HTTP_PROXY || NGX_HTTP_REALIP || NGX_HTTP_GEO)
		NGX_HTTP_CLOJURE_HEADERSI_X_FORWARDED_FOR_OFFSET = MEM_INDEX[NGX_HTTP_CLOJURE_HEADERSI_X_FORWARDED_FOR_IDX];
		//#endif

		//#if (NGX_HTTP_REALIP)
		NGX_HTTP_CLOJURE_HEADERSI_X_REAL_IP_OFFSET = MEM_INDEX[NGX_HTTP_CLOJURE_HEADERSI_X_REAL_IP_IDX];
		//#endif

		//#if (NGX_HTTP_HEADERS)
		NGX_HTTP_CLOJURE_HEADERSI_ACCEPT_OFFSET = MEM_INDEX[NGX_HTTP_CLOJURE_HEADERSI_ACCEPT_IDX];
		NGX_HTTP_CLOJURE_HEADERSI_ACCEPT_LANGUAGE_OFFSET = MEM_INDEX[NGX_HTTP_CLOJURE_HEADERSI_ACCEPT_LANGUAGE_IDX];
		//#endif

		//#if (NGX_HTTP_DAV)
		NGX_HTTP_CLOJURE_HEADERSI_DEPTH_OFFSET = MEM_INDEX[NGX_HTTP_CLOJURE_HEADERSI_DEPTH_IDX];
		NGX_HTTP_CLOJURE_HEADERSI_DESTINATION_OFFSET = MEM_INDEX[NGX_HTTP_CLOJURE_HEADERSI_DESTINATION_IDX];
		NGX_HTTP_CLOJURE_HEADERSI_OVERWRITE_OFFSET = MEM_INDEX[NGX_HTTP_CLOJURE_HEADERSI_OVERWRITE_IDX];
		NGX_HTTP_CLOJURE_HEADERSI_DATE_OFFSET = MEM_INDEX[NGX_HTTP_CLOJURE_HEADERSI_DATE_IDX];
		//#endif

		NGX_HTTP_CLOJURE_HEADERSI_USER_OFFSET = MEM_INDEX[NGX_HTTP_CLOJURE_HEADERSI_USER_IDX];
		NGX_HTTP_CLOJURE_HEADERSI_PASSWD_OFFSET = MEM_INDEX[NGX_HTTP_CLOJURE_HEADERSI_PASSWD_IDX];
		NGX_HTTP_CLOJURE_HEADERSI_COOKIE_OFFSET =MEM_INDEX[NGX_HTTP_CLOJURE_HEADERSI_COOKIE_IDX];
		NGX_HTTP_CLOJURE_HEADERSI_SERVER_OFFSET = MEM_INDEX[NGX_HTTP_CLOJURE_HEADERSI_SERVER_IDX];
		NGX_HTTP_CLOJURE_HEADERSI_CONTENT_LENGTH_N_OFFSET = MEM_INDEX[NGX_HTTP_CLOJURE_HEADERSI_CONTENT_LENGTH_N_IDX];
		NGX_HTTP_CLOJURE_HEADERSI_KEEP_ALIVE_N_OFFSET = MEM_INDEX[NGX_HTTP_CLOJURE_HEADERSI_KEEP_ALIVE_N_IDX];
		NGX_HTTP_CLOJURE_HEADERSI_HEADERS_OFFSET = MEM_INDEX[NGX_HTTP_CLOJURE_HEADERSI_HEADERS_IDX];


		/*index for size of ngx_http_headers_out_t */
		NGX_HTTP_CLOJURE_HEADERSO_SIZE = MEM_INDEX[NGX_HTTP_CLOJURE_HEADERSO_SIZE_IDX];
		/*field offset index for ngx_http_headers_out_t*/
		NGX_HTTP_CLOJURE_HEADERSO_STATUS_OFFSET = MEM_INDEX[NGX_HTTP_CLOJURE_HEADERSO_STATUS_IDX];
		NGX_HTTP_CLOJURE_HEADERSO_STATUS_LINE_OFFSET = MEM_INDEX[NGX_HTTP_CLOJURE_HEADERSO_STATUS_LINE_IDX];
		NGX_HTTP_CLOJURE_HEADERSO_SERVER_OFFSET = MEM_INDEX[NGX_HTTP_CLOJURE_HEADERSO_SERVER_IDX];
		NGX_HTTP_CLOJURE_HEADERSO_DATE_OFFSET = MEM_INDEX[NGX_HTTP_CLOJURE_HEADERSO_DATE_IDX];
		NGX_HTTP_CLOJURE_HEADERSO_CONTENT_LENGTH_OFFSET = MEM_INDEX[NGX_HTTP_CLOJURE_HEADERSO_CONTENT_LENGTH_IDX];
		NGX_HTTP_CLOJURE_HEADERSO_CONTENT_ENCODING_OFFSET = MEM_INDEX[NGX_HTTP_CLOJURE_HEADERSO_CONTENT_ENCODING_IDX];
		NGX_HTTP_CLOJURE_HEADERSO_LOCATION_OFFSET = MEM_INDEX[NGX_HTTP_CLOJURE_HEADERSO_LOCATION_IDX];
		NGX_HTTP_CLOJURE_HEADERSO_REFRESH_OFFSET = MEM_INDEX[NGX_HTTP_CLOJURE_HEADERSO_REFRESH_IDX];
		NGX_HTTP_CLOJURE_HEADERSO_LAST_MODIFIED_OFFSET = MEM_INDEX[NGX_HTTP_CLOJURE_HEADERSO_LAST_MODIFIED_IDX];
		NGX_HTTP_CLOJURE_HEADERSO_CONTENT_RANGE_OFFSET = MEM_INDEX[NGX_HTTP_CLOJURE_HEADERSO_CONTENT_RANGE_IDX];
		NGX_HTTP_CLOJURE_HEADERSO_ACCEPT_RANGES_OFFSET = MEM_INDEX[NGX_HTTP_CLOJURE_HEADERSO_ACCEPT_RANGES_IDX];
		NGX_HTTP_CLOJURE_HEADERSO_WWW_AUTHENTICATE_OFFSET = MEM_INDEX[NGX_HTTP_CLOJURE_HEADERSO_WWW_AUTHENTICATE_IDX];
		NGX_HTTP_CLOJURE_HEADERSO_EXPIRES_OFFSET = MEM_INDEX[NGX_HTTP_CLOJURE_HEADERSO_EXPIRES_IDX];
		NGX_HTTP_CLOJURE_HEADERSO_ETAG_OFFSET = MEM_INDEX[NGX_HTTP_CLOJURE_HEADERSO_ETAG_IDX];
		NGX_HTTP_CLOJURE_HEADERSO_OVERRIDE_CHARSET_OFFSET = MEM_INDEX[NGX_HTTP_CLOJURE_HEADERSO_OVERRIDE_CHARSET_IDX];
		NGX_HTTP_CLOJURE_HEADERSO_CONTENT_TYPE_LEN_OFFSET = MEM_INDEX[NGX_HTTP_CLOJURE_HEADERSO_CONTENT_TYPE_LEN_IDX];
		NGX_HTTP_CLOJURE_HEADERSO_CONTENT_TYPE_OFFSET = MEM_INDEX[NGX_HTTP_CLOJURE_HEADERSO_CONTENT_TYPE_IDX];
		NGX_HTTP_CLOJURE_HEADERSO_CHARSET_OFFSET = MEM_INDEX[NGX_HTTP_CLOJURE_HEADERSO_CHARSET_IDX];
		NGX_HTTP_CLOJURE_HEADERSO_CONTENT_TYPE_LOWCASE_OFFSET = MEM_INDEX[NGX_HTTP_CLOJURE_HEADERSO_CONTENT_TYPE_LOWCASE_IDX];
		NGX_HTTP_CLOJURE_HEADERSO_CONTENT_TYPE_HASH_OFFSET = MEM_INDEX[NGX_HTTP_CLOJURE_HEADERSO_CONTENT_TYPE_HASH_IDX];
		NGX_HTTP_CLOJURE_HEADERSO_CACHE_CONTROL_OFFSET = MEM_INDEX[NGX_HTTP_CLOJURE_HEADERSO_CACHE_CONTROL_IDX];
		NGX_HTTP_CLOJURE_HEADERSO_CONTENT_LENGTH_N_OFFSET = MEM_INDEX[NGX_HTTP_CLOJURE_HEADERSO_CONTENT_LENGTH_N_IDX];
		NGX_HTTP_CLOJURE_HEADERSO_DATE_TIME_OFFSET = MEM_INDEX[NGX_HTTP_CLOJURE_HEADERSO_DATE_TIME_IDX];
		NGX_HTTP_CLOJURE_HEADERSO_LAST_MODIFIED_TIME_OFFSET = MEM_INDEX[NGX_HTTP_CLOJURE_HEADERSO_LAST_MODIFIED_TIME_IDX];
		NGX_HTTP_CLOJURE_HEADERSO_HEADERS_OFFSET = MEM_INDEX[NGX_HTTP_CLOJURE_HEADERSO_HEADERS_IDX];
		
		NGX_HTTP_CLOJURE_PTR_SIZE = MEM_INDEX[NGX_HTTP_CLOJURE_PTR_SIZE_IDX];
		
		KNOWN_REQ_HEADERS.put("host", NGX_HTTP_CLOJURE_HEADERSI_HOST_OFFSET);
		KNOWN_REQ_HEADERS.put("connection", NGX_HTTP_CLOJURE_HEADERSI_CONNECTION_OFFSET);
		KNOWN_REQ_HEADERS.put("if-modified-since", NGX_HTTP_CLOJURE_HEADERSI_IF_MODIFIED_SINCE_OFFSET);
		KNOWN_REQ_HEADERS.put("if-unmodified-since", NGX_HTTP_CLOJURE_HEADERSI_IF_UNMODIFIED_SINCE_OFFSET);
		KNOWN_REQ_HEADERS.put("user-agent", NGX_HTTP_CLOJURE_HEADERSI_USER_AGENT_OFFSET);
		KNOWN_REQ_HEADERS.put("referer", NGX_HTTP_CLOJURE_HEADERSI_REFERER_OFFSET);
		KNOWN_REQ_HEADERS.put("content-length", NGX_HTTP_CLOJURE_HEADERSI_CONTENT_LENGTH_OFFSET);
		KNOWN_REQ_HEADERS.put("content-type", NGX_HTTP_CLOJURE_HEADERSI_CONTENT_TYPE_OFFSET);
		KNOWN_REQ_HEADERS.put("range", NGX_HTTP_CLOJURE_HEADERSI_RANGE_OFFSET);
		KNOWN_REQ_HEADERS.put("if-range", NGX_HTTP_CLOJURE_HEADERSI_IF_RANGE_OFFSET);
		KNOWN_REQ_HEADERS.put("transfer-encoding", NGX_HTTP_CLOJURE_HEADERSI_TRANSFER_ENCODING_OFFSET);
		KNOWN_REQ_HEADERS.put("expect", NGX_HTTP_CLOJURE_HEADERSI_EXPECT_OFFSET);
		KNOWN_REQ_HEADERS.put("accept-encoding", NGX_HTTP_CLOJURE_HEADERSI_ACCEPT_ENCODING_OFFSET);
		KNOWN_REQ_HEADERS.put("via", NGX_HTTP_CLOJURE_HEADERSI_VIA_OFFSET);
		KNOWN_REQ_HEADERS.put("authorization", NGX_HTTP_CLOJURE_HEADERSI_AUTHORIZATION_OFFSET);
		KNOWN_REQ_HEADERS.put("keep-alive", NGX_HTTP_CLOJURE_HEADERSI_KEEP_ALIVE_OFFSET);
		KNOWN_REQ_HEADERS.put("x-forwarded-for", NGX_HTTP_CLOJURE_HEADERSI_X_FORWARDED_FOR_OFFSET);
		KNOWN_REQ_HEADERS.put("x-real-ip", NGX_HTTP_CLOJURE_HEADERSI_X_REAL_IP_OFFSET);
		KNOWN_REQ_HEADERS.put("accept", NGX_HTTP_CLOJURE_HEADERSI_ACCEPT_OFFSET);

		KNOWN_REQ_HEADERS.put("accept-language", NGX_HTTP_CLOJURE_HEADERSI_ACCEPT_LANGUAGE_OFFSET);
		KNOWN_REQ_HEADERS.put("depth", NGX_HTTP_CLOJURE_HEADERSI_DEPTH_OFFSET);
		KNOWN_REQ_HEADERS.put("destination", NGX_HTTP_CLOJURE_HEADERSI_DESTINATION_OFFSET);
		KNOWN_REQ_HEADERS.put("overwrite", NGX_HTTP_CLOJURE_HEADERSI_OVERWRITE_OFFSET);
		KNOWN_REQ_HEADERS.put("date", NGX_HTTP_CLOJURE_HEADERSI_DATE_OFFSET);

		KNOWN_REQ_HEADERS.put("cookie", NGX_HTTP_CLOJURE_HEADERSI_COOKIE_OFFSET);

		for (int i = 0; i < NGX_HTTP_CLOJURE_CORE_VARIABLES_LEN; i++) {
			long addr = NGX_HTTP_CLOJURE_CORE_VARIABLES_ADDR + i * NGX_HTTP_CLOJURE_STR_SIZE;
			CORE_VARS.put(fetchNGXString(addr, DEFAULT_ENCODING), addr);
		}
		
		SERVER_PORT_FETCHER = new RequestKnownNameVarFetcher("server_port");
		SERVER_NAME_FETCHER = new RequestKnownNameVarFetcher("server_name");
		REMOTE_ADDR_FETCHER = new RequestKnownNameVarFetcher("remote_addr");
		URI_FETCHER = new RequestKnownOffsetVarFetcher(NGX_HTTP_CLOJURE_REQ_URI_OFFSET);
		QUERY_STRING_FETCHER = new RequestKnownOffsetVarFetcher(NGX_HTTP_CLOJURE_REQ_ARGS_OFFSET);
		SCHEME_FETCHER = new RequestKnownNameVarFetcher("scheme");
		REQUEST_METHOD_FETCHER = new RequestMethodFetcher();
		CONTENT_TYPE_FETCHER = new RequestKnownHeaderFetcher("content-type");
		CHARACTER_ENCODING_FETCHER = new RequestCharacterEncodingFetcher();
		HEADER_FETCHER = new RequestHeaderFetcher();
	}
	
	
	public static synchronized int registerCode(long codePtr, long len) {
		if (CODE_MAP.containsKey(codePtr)) {
			return CODE_MAP.get(codePtr);
		}
		String code = fetchString(codePtr, (int)len, DEFAULT_ENCODING);
		IFn f = (IFn)RT.var("clojure.core", "eval").invoke(RT.var("clojure.core","read-string").invoke(code));
		HANDLERS.add(f);
		return HANDLERS.size() - 1;
	}
	
	/**
	 * convert ngx_str_t to  java String
	 */
	public static final String fetchNGXString(long address, Charset encoding) {
		if (address == 0){
			return null;
		}
		long lenAddr = address + NGX_HTTP_CLOJURE_STR_LEN_OFFSET;
		int len = fetchNGXInt(lenAddr);
		if (len == 0){
			return null;
		}
		return fetchString(address + NGX_HTTP_CLOJURE_STR_DATA_OFFSET, len, encoding);
	}
	
	public static final void pushNGXString(long address, String val, Charset encoding, long pool){
			long lenAddr = address + NGX_HTTP_CLOJURE_STR_LEN_OFFSET;
			long dataAddr = address + NGX_HTTP_CLOJURE_STR_DATA_OFFSET;
			pushNGXInt(lenAddr, pushString(dataAddr, val, encoding, pool));
	}
	
	
	public static final int fetchNGXInt(long address){
		return NGX_HTTP_CLOJURE_UINT_SIZE == 4 ? UNSAFE.getInt(address) : (int)UNSAFE.getLong(address);
	}
	
	public static final void pushNGXInt(long address, int val){
		if (NGX_HTTP_CLOJURE_UINT_SIZE == 4){
			UNSAFE.putInt(address, val);
		}else {
			UNSAFE.putLong(address, val);
		}
	}
	
	public static final void pushNGXOfft(long address, int val){
		if (NGX_HTTP_CLOJURE_OFFT_SIZE == 4){
			UNSAFE.putInt(address, val);
		}else {
			UNSAFE.putLong(address, val);
		}
	}
	
	
	//TODO: for better performance to use direct encoder instead of bytes copy
	public static final String fetchString(long address, int size, Charset encoding) {
		byte[] buf = new byte[size];
		ngx_http_clojure_mem_copy_to_obj(UNSAFE.getAddress(address), buf, BYTE_ARRAY_OFFSET, size);
		return new String(buf, encoding);

	}
	
	
	public static final int pushString(long address, String val, Charset encoding, long pool) {
		byte[] bytes;
		bytes = val.getBytes(encoding);
		long strAddr = ngx_palloc(pool, bytes.length);
		UNSAFE.putAddress(address, strAddr);
		ngx_http_clojure_mem_copy_to_addr(bytes, BYTE_ARRAY_OFFSET, strAddr, bytes.length);
		return bytes.length;
	}
	
	
	public static int eval(int codeId, long r) {
		IFn f = HANDLERS.get(codeId);
		LazyRequestMap req = new LazyRequestMap(r);
		try{
			long pool = UNSAFE.getAddress(r + NGX_HTTP_CLOJURE_REQ_POOL_OFFSET);
			Map resp = (Map) f.invoke(req);
			Object statusObj = resp.get(STATUS);
			int status = 200;
			if (statusObj != null) {
				if (statusObj instanceof Number){
					status = ((Number)statusObj).intValue();
				}else {
					status = Integer.parseInt(statusObj.toString());
				}
			}
			long headers_out = r + NGX_HTTP_CLOJURE_REQ_HEADERS_OUT_OFFSET;
			pushNGXInt(headers_out + NGX_HTTP_CLOJURE_HEADERSO_STATUS_OFFSET, status);
			Map headers = (Map) resp.get(HEADERS);
			String contentType = (String) headers.get("content-type");
			
			//TODO: should use ngx_http_set_content_type(r) to get the default content-type by MIME-TYPE
			if (contentType == null){
				contentType = "text/html; charset=UTF-8";
			}
			pushNGXString(headers_out + NGX_HTTP_CLOJURE_HEADERSO_CONTENT_TYPE_OFFSET, contentType, DEFAULT_ENCODING, pool);
			
			Object body = resp.get(BODY);
			
			long b = 0;
			if (body instanceof String) {
				String bodyStr = (String) body;
				byte[] bytes = bodyStr.getBytes(DEFAULT_ENCODING);
				pushNGXOfft(headers_out + NGX_HTTP_CLOJURE_HEADERSO_CONTENT_LENGTH_N_OFFSET, bytes.length);
				b = ngx_create_temp_buf(pool, bytes.length);
				ngx_http_clojure_mem_init_ngx_buf(b, bytes, BYTE_ARRAY_OFFSET, bytes.length, 1);
			}else if (body instanceof File) {
				if (! ((File)body).exists() ) {
					return 400;
				}
				byte[] bytes = ((File)body).getPath().getBytes();
				long file = ngx_pcalloc(pool, bytes.length+1);
				ngx_http_clojure_mem_copy_to_addr(bytes, BYTE_ARRAY_OFFSET, file, bytes.length);
				b = ngx_create_file_buf(r, file, bytes.length);
				if (b == 0){
					return 500;
				}
			}
			int rc = (int)ngx_http_send_header(r);
			if (rc == NGX_ERROR || rc > NGX_OK){
				return rc;
			}
			long chain = ngx_palloc(pool, NGX_HTTP_CLOJURE_CHAIN_SIZE);
			UNSAFE.putAddress(chain + NGX_HTTP_CLOJURE_CHAIN_BUF_OFFSET, b);
			UNSAFE.putAddress(chain + NGX_HTTP_CLOJURE_CHAIN_NEXT_OFFSET, 0);
			return (int)ngx_http_output_filter(r, chain);
		}catch(Throwable e){
			e.printStackTrace();
			return 500;
		}
		
		
	}
}
